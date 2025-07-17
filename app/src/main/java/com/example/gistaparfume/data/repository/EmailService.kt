package com.example.gistaparfume.data.repository

/**
 * Interface for email service functionality
 */
interface EmailService {
    /**
     * Sends a password reset email to the specified email address
     * @param email The recipient email address
     * @param resetToken The password reset token
     * @return Result indicating success or failure
     */
    suspend fun sendPasswordResetEmail(email: String, resetToken: String): Result<Unit>
}