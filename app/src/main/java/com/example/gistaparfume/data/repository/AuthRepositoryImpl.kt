package com.example.gistaparfume.data.repository

import android.content.Context
import android.util.Log
import com.example.gistaparfume.data.AuthResult
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.dao.UserDao
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.utils.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Concrete implementation of AuthRepository
 */
class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val emailService: EmailService,
    private val applicationContext: Context // Use applicationContext to avoid memory leaks
) : AuthRepository {
    
    // Authentication state manager for persistence
    private val authStateManager = AuthStateManager.getInstance(applicationContext)
    
    // Password reset token DAO
    private val passwordResetTokenDao = AppDatabaseConfig.getDatabase(applicationContext).passwordResetTokenDao()
    
    // Store current authenticated user in memory
    private var currentUser: UserEntity? = null
    
    /**
     * Authenticates a user with email and password
     */
    override suspend fun authenticateUser(email: String, password: String): AuthResult {
        Log.d("AuthRepository", "Email: $email, Password: $password")
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository", "Attempting login for email: ${email.trim().lowercase()}")
                
                // Find user by email
                val user = userDao.findByEmail(email.trim().lowercase())

                // Check if user exists
                if (user == null) {
                    Log.w("AuthRepository", "User not found for email: ${email.trim().lowercase()}")
                    return@withContext AuthResult.EmailNotFound
                }
                
                Log.d("AuthRepository", "User found: ${user.email}, role: ${user.role}, active: ${user.isActive} , password: ${user.password}")
                
                // Check if user is active
                if (!user.isActive) {
                    Log.w("AuthRepository", "User account is not active")
                    return@withContext AuthResult.Error("Akun Anda tidak aktif")
                }
                
                // Validate password
                val isPasswordEncrypted = PasswordUtils.isPasswordEncrypted(user.password)
                Log.d("AuthRepository", "Password is encrypted: $isPasswordEncrypted")
                
                val isPasswordValid = if (isPasswordEncrypted) {
                    // Password is encrypted, use BCrypt validation
                    Log.d("AuthRepository", "Using BCrypt validation")
                    PasswordUtils.validatePassword(password, user.password)
                } else {
                    // Legacy password (plain text or SHA-256), direct comparison for now
                    // This handles existing users before BCrypt implementation
                    Log.d("AuthRepository", "Using direct password comparison")
                    user.password == password
                }
                
                Log.d("AuthRepository", "Password validation result: $isPasswordValid")
                
                if (!isPasswordValid) {
                    Log.w("AuthRepository", "Password validation failed")
                    return@withContext AuthResult.InvalidPassword
                }
                
                // Authentication successful
                Log.d("AuthRepository", "Authentication successful for user: ${user.email}")
                currentUser = user
                // Save authentication state to DataStore
                authStateManager.saveAuthState(user)
                AuthResult.Success(user)
                
            } catch (e: Exception) {
                Log.e("AuthRepository", "Authentication error", e)
                AuthResult.Error("Terjadi kesalahan saat melakukan autentikasi: ${e.message}")
            }
        }
    }
    
    /**
     * Sends a password reset email to the specified email address
     */
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Check if user exists
                val user = userDao.findByEmail(email.trim().lowercase())
                if (user == null) {
                    return@withContext Result.failure(Exception("Email tidak terdaftar"))
                }
                
                // Generate reset token and store it in database
                val resetToken = generateResetToken()
                val expiryTime = System.currentTimeMillis() + (15 * 60 * 1000) // 15 minutes from now
                
                // Store token in database
                val tokenEntity = com.example.gistaparfume.data.entity.PasswordResetTokenEntity(
                    email = email.trim().lowercase(),
                    token = resetToken,
                    expiryTime = expiryTime
                )
                passwordResetTokenDao.insertToken(tokenEntity)
                
                // Send email using email service
                emailService.sendPasswordResetEmail(email, resetToken)
                
            } catch (e: Exception) {
                Result.failure(Exception("Gagal mengirim email reset password: ${e.message}"))
            }
        }
    }
    
    /**
     * Updates a user's password
     */
    override suspend fun updatePassword(email: String, newPassword: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Find user by email
                val user = userDao.findByEmail(email.trim().lowercase())
                if (user == null) {
                    return@withContext Result.failure(Exception("User tidak ditemukan"))
                }
                
                // Encrypt new password
                val encryptedPassword = PasswordUtils.encryptPassword(newPassword)
                
                // Update password in database
                val updatedUser = user.copy(password = encryptedPassword)
                userDao.updateUser(updatedUser)
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Result.failure(Exception("Gagal mengupdate password: ${e.message}"))
            }
        }
    }
    
    /**
     * Validates a password reset token
     */
    override suspend fun validateResetToken(email: String, resetToken: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Clean up expired tokens first
                passwordResetTokenDao.deleteExpiredTokens(System.currentTimeMillis())
                
                // Find token by email
                val tokenEntity = passwordResetTokenDao.getTokenByEmail(email.trim().lowercase())
                
                if (tokenEntity == null) {
                    return@withContext Result.failure(Exception("Kode reset tidak valid atau sudah kedaluwarsa"))
                }
                
                // Check if token matches
                if (tokenEntity.token != resetToken.trim()) {
                    return@withContext Result.failure(Exception("Kode reset tidak valid"))
                }
                
                // Check if token is expired
                if (tokenEntity.expiryTime < System.currentTimeMillis()) {
                    // Delete expired token
                    passwordResetTokenDao.deleteTokenByEmail(email.trim().lowercase())
                    return@withContext Result.failure(Exception("Kode reset sudah kedaluwarsa"))
                }
                
                Result.success(Unit)
                
            } catch (e: Exception) {
                Result.failure(Exception("Gagal memvalidasi kode reset: ${e.message}"))
            }
        }
    }
    
    /**
     * Resets password using a valid reset token
     */
    override suspend fun resetPasswordWithToken(email: String, resetToken: String, newPassword: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // First validate the token
                val tokenValidation = validateResetToken(email, resetToken)
                if (tokenValidation.isFailure) {
                    return@withContext tokenValidation
                }
                
                // Find user by email
                val user = userDao.findByEmail(email.trim().lowercase())
                if (user == null) {
                    return@withContext Result.failure(Exception("User tidak ditemukan"))
                }
                
                // Encrypt new password
                val encryptedPassword = PasswordUtils.encryptPassword(newPassword)
                
                // Update password in database
                val updatedUser = user.copy(password = encryptedPassword)
                userDao.updateUser(updatedUser)
                
                // Delete the used token
                passwordResetTokenDao.deleteTokenByEmail(email.trim().lowercase())
                
                Log.d("AuthRepository", "Password reset successful for user: ${user.email}")
                Result.success(Unit)
                
            } catch (e: Exception) {
                Log.e("AuthRepository", "Password reset failed", e)
                Result.failure(Exception("Gagal mereset password: ${e.message}"))
            }
        }
    }
    
    /**
     * Gets the currently authenticated user
     * First checks memory, then restores from DataStore if needed
     */
    override suspend fun getCurrentUser(): UserEntity? {
        return withContext(Dispatchers.IO) {
            // If user is in memory, return it
            if (currentUser != null) {
                return@withContext currentUser
            }
            
            // Try to restore from DataStore
            val storedUser = authStateManager.getAuthState()
            if (storedUser != null) {
                currentUser = storedUser
                return@withContext storedUser
            }
            
            null
        }
    }
    
    /**
     * Logs out the current user by clearing authentication state
     */
    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            currentUser = null
            // Clear authentication state from DataStore
            authStateManager.clearAuthState()
        }
    }
    
    /**
     * Generates a simple reset token
     * In production, this should be more secure with proper expiration
     */
    private fun generateResetToken(): String {
        return UUID.randomUUID().toString()
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepositoryImpl? = null

        fun getInstance(context: Context, emailService: EmailService): AuthRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                // Use applicationContext to avoid memory leaks
                val applicationContext = context.applicationContext
                val database = AppDatabaseConfig.getDatabase(applicationContext)
                val instance = AuthRepositoryImpl(database.userDao(), emailService, applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}