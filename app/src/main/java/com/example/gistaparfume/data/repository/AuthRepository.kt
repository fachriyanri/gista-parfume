package com.example.gistaparfume.data.repository

import com.example.gistaparfume.data.AuthResult
import com.example.gistaparfume.data.entity.UserEntity

/**
 * Repository interface for authentication operations
 */
interface AuthRepository {
    /**
     * Authenticates a user with email and password
     * @param email User's email address
     * @param password User's plain text password
     * @return AuthResult indicating success or failure with appropriate error type
     */
    suspend fun authenticateUser(email: String, password: String): AuthResult
    
    /**
     * Sends a password reset email to the specified email address
     * @param email Email address to send reset email to
     * @return Result<Unit> indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    
    /**
     * Updates a user's password
     * @param email User's email address
     * @param newPassword New plain text password (will be encrypted)
     * @return Result<Unit> indicating success or failure
     */
    suspend fun updatePassword(email: String, newPassword: String): Result<Unit>
    
    /**
     * Validates a password reset token
     * @param email User's email address
     * @param resetToken The reset token to validate
     * @return Result<Unit> indicating if token is valid
     */
    suspend fun validateResetToken(email: String, resetToken: String): Result<Unit>
    
    /**
     * Resets password using a valid reset token
     * @param email User's email address
     * @param resetToken The reset token
     * @param newPassword New plain text password (will be encrypted)
     * @return Result<Unit> indicating success or failure
     */
    suspend fun resetPasswordWithToken(email: String, resetToken: String, newPassword: String): Result<Unit>
    
    /**
     * Gets the currently authenticated user
     * @return UserEntity? - Current user if authenticated, null otherwise
     */
    suspend fun getCurrentUser(): UserEntity?
    
    /**
     * Logs out the current user by clearing authentication state
     */
    suspend fun logout()
}