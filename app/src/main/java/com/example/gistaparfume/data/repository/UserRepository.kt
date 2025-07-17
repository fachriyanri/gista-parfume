package com.example.gistaparfume.data.repository

import android.content.Context
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.dao.UserDao
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import com.example.gistaparfume.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val applicationContext: Context // Use applicationContext to avoid memory leaks
) {
    
    /**
     * Registers a new user in the database
     * @param name User's full name
     * @param email User's email address
     * @param password User's plain text password (will be encrypted)
     * @return Result<Long> - Success with user ID or failure with exception
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input parameters
                if (name.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Nama tidak boleh kosong"))
                }
                
                if (email.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Email tidak boleh kosong"))
                }
                
                if (password.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Password tidak boleh kosong"))
                }
                
                // Validate email format
                if (!isValidEmail(email)) {
                    return@withContext Result.failure(InvalidEmailFormatException("Email tidak sesuai format"))
                }
                
                // Validate password length
                if (password.length < 8) {
                    return@withContext Result.failure(InvalidPasswordException("Input password sesuai dengan kriteria"))
                }
                
                // Check if email already exists with better error handling
                val existingUser = try {
                    userDao.findByEmail(email)
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat terhubung ke database. Periksa koneksi Anda dan coba lagi."))
                }
                
                if (existingUser != null) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah terdaftar"))
                }
                
                // Encrypt password
                val encryptedPassword = try {
                    PasswordUtils.encryptPassword(password)
                } catch (e: Exception) {
                    return@withContext Result.failure(EncryptionException("Terjadi kesalahan saat mengenkripsi password"))
                }
                
                // Create user entity
                val userEntity = UserEntity(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = encryptedPassword,
                    role = UserRole.MEMBER,
                    isActive = true,
                    image = null
                )
                
                // Insert user into database with better error handling
                val userId = try {
                    userDao.insertUser(userEntity)
                } catch (e: android.database.sqlite.SQLiteConstraintException) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah terdaftar"))
                } catch (e: android.database.sqlite.SQLiteException) {
                    return@withContext Result.failure(DatabaseException("Terjadi kesalahan database. Silakan coba lagi."))
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat menyimpan data. Periksa koneksi dan coba lagi."))
                }
                
                Result.success(userId)
                
            } catch (e: Exception) {
                when (e) {
                    is EmailAlreadyExistsException,
                    is InvalidEmailFormatException,
                    is InvalidPasswordException,
                    is InvalidInputException,
                    is DatabaseException,
                    is DatabaseConnectionException,
                    is EncryptionException -> Result.failure(e)
                    else -> Result.failure(UnexpectedErrorException("Terjadi kesalahan yang tidak terduga. Silakan coba lagi."))
                }
            }
        }
    }
    
    /**
     * Checks if an email address is available for registration
     * @param email Email address to check
     * @return Boolean - true if email is available, false if already exists
     */
    suspend fun isEmailAvailable(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val existingUser = userDao.findByEmail(email)
                existingUser == null
            } catch (e: Exception) {
                // In case of database error, assume email is not available for safety
                false
            }
        }
    }
    
    /**
     * Finds a user by email address
     * @param email Email address to search for
     * @return UserEntity? - User if found, null otherwise
     */
    suspend fun findUserByEmail(email: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.findByEmail(email)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Validates email format using regex
     * @param email Email address to validate
     * @return Boolean - true if valid format, false otherwise
     */
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }
    
    // New methods for user management operations
    
    /**
     * Gets all users with pagination
     * @param page Page number (1-based)
     * @param pageSize Number of users per page
     * @return List<UserEntity> - List of users for the specified page
     */
    suspend fun getAllUsers(page: Int, pageSize: Int): List<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val offset = (page - 1) * pageSize
                userDao.getAllUsersPaginated(pageSize, offset)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    /**
     * Searches users by name or email with pagination
     * @param query Search query to match against name or email
     * @param page Page number (1-based)
     * @param pageSize Number of users per page
     * @return List<UserEntity> - List of matching users for the specified page
     */
    suspend fun searchUsers(query: String, page: Int, pageSize: Int): List<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val offset = (page - 1) * pageSize
                val searchQuery = "%$query%"
                userDao.searchUsers(searchQuery, pageSize, offset)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
    
    /**
     * Gets a user by ID
     * @param id User ID
     * @return UserEntity? - User if found, null otherwise
     */
    suspend fun getUserById(id: Int): UserEntity? {
        return withContext(Dispatchers.IO) {
            try {
                userDao.getUserById(id)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Deletes a user by ID
     * @param id User ID to delete
     * @return Result<Unit> - Success or failure result
     */
    suspend fun deleteUser(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.deleteUser(id)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(DatabaseException("Gagal menghapus pengguna. Silakan coba lagi."))
            }
        }
    }
    
    /**
     * Gets total count of all users
     * @return Int - Total number of users
     */
    suspend fun getTotalUserCount(): Int {
        return withContext(Dispatchers.IO) {
            try {
                userDao.getTotalUserCount()
            } catch (e: Exception) {
                0
            }
        }
    }
    
    /**
     * Gets count of users matching search query
     * @param query Search query to match against name or email
     * @return Int - Number of matching users
     */
    suspend fun getSearchResultCount(query: String): Int {
        return withContext(Dispatchers.IO) {
            try {
                val searchQuery = "%$query%"
                userDao.getSearchResultCount(searchQuery)
            } catch (e: Exception) {
                0
            }
        }
    }
    
    /**
     * Creates a new user
     * @param user UserEntity to create
     * @return Result<Long> - Success with user ID or failure with exception
     */
    suspend fun createUser(user: UserEntity): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (user.name.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Nama tidak boleh kosong"))
                }
                
                if (user.email.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Email tidak boleh kosong"))
                }
                
                if (user.password.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Password tidak boleh kosong"))
                }
                
                // Validate email format
                if (!isValidEmail(user.email)) {
                    return@withContext Result.failure(InvalidEmailFormatException("Email tidak sesuai format"))
                }
                
                // Check if email already exists
                val existingUser = try {
                    userDao.findByEmail(user.email)
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat terhubung ke database"))
                }
                
                if (existingUser != null) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah terdaftar"))
                }
                
                // Insert user into database
                val userId = try {
                    userDao.insertUser(user)
                } catch (e: android.database.sqlite.SQLiteConstraintException) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah terdaftar"))
                } catch (e: android.database.sqlite.SQLiteException) {
                    return@withContext Result.failure(DatabaseException("Terjadi kesalahan database"))
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat menyimpan data"))
                }
                
                Result.success(userId)
                
            } catch (e: Exception) {
                when (e) {
                    is EmailAlreadyExistsException,
                    is InvalidEmailFormatException,
                    is InvalidInputException,
                    is DatabaseException,
                    is DatabaseConnectionException -> Result.failure(e)
                    else -> Result.failure(UnexpectedErrorException("Terjadi kesalahan yang tidak terduga"))
                }
            }
        }
    }
    
    /**
     * Updates an existing user
     * @param user UserEntity to update
     * @return Result<Unit> - Success or failure result
     */
    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Validate input
                if (user.name.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Nama tidak boleh kosong"))
                }
                
                if (user.email.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Email tidak boleh kosong"))
                }
                
                if (user.password.isBlank()) {
                    return@withContext Result.failure(InvalidInputException("Password tidak boleh kosong"))
                }
                
                // Validate email format
                if (!isValidEmail(user.email)) {
                    return@withContext Result.failure(InvalidEmailFormatException("Email tidak sesuai format"))
                }
                
                // Check if email already exists for another user
                val existingUser = try {
                    userDao.findByEmail(user.email)
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat terhubung ke database"))
                }
                
                if (existingUser != null && existingUser.id != user.id) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah digunakan oleh pengguna lain"))
                }
                
                // Update user in database
                try {
                    userDao.updateUser(user)
                } catch (e: android.database.sqlite.SQLiteConstraintException) {
                    return@withContext Result.failure(EmailAlreadyExistsException("Email ini sudah digunakan oleh pengguna lain"))
                } catch (e: android.database.sqlite.SQLiteException) {
                    return@withContext Result.failure(DatabaseException("Terjadi kesalahan database"))
                } catch (e: Exception) {
                    return@withContext Result.failure(DatabaseConnectionException("Tidak dapat memperbarui data"))
                }
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                when (e) {
                    is EmailAlreadyExistsException,
                    is InvalidEmailFormatException,
                    is InvalidInputException,
                    is DatabaseException,
                    is DatabaseConnectionException -> Result.failure(e)
                    else -> Result.failure(UnexpectedErrorException("Terjadi kesalahan yang tidak terduga"))
                }
            }
        }
    }

    
    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                // Use applicationContext to avoid memory leaks
                val applicationContext = context.applicationContext
                val database = AppDatabaseConfig.getDatabase(applicationContext)
                val instance = UserRepository(database.userDao(), applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

// Custom exceptions for better error handling
class EmailAlreadyExistsException(message: String) : Exception(message)
class InvalidEmailFormatException(message: String) : Exception(message)
class InvalidPasswordException(message: String) : Exception(message)
class InvalidInputException(message: String) : Exception(message)
class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DatabaseConnectionException(message: String) : Exception(message)
class EncryptionException(message: String) : Exception(message)
class UnexpectedErrorException(message: String) : Exception(message)