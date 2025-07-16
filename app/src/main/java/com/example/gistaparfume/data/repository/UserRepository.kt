package com.example.gistaparfume.data.repository

import android.content.Context
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.dao.UserDao
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao,
    private val context: Context
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
                    encryptPassword(password)
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
    
    /**
     * Encrypts password using SHA-256 hashing
     * @param password Plain text password
     * @return String - Encrypted password
     */
    private fun encryptPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabaseConfig.getDatabase(context)
                val instance = UserRepository(database.userDao(), context)
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