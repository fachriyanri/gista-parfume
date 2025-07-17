package com.example.gistaparfume.utils

import at.favre.lib.crypto.bcrypt.BCrypt

/**
 * Utility object for password encryption and validation using BCrypt
 * Uses Android-compatible BCrypt library
 */
object PasswordUtils {
    
    // BCrypt cost factor (rounds) - 12 provides good security vs performance balance
    private const val BCRYPT_COST = 12
    
    /**
     * Encrypts a plain text password using BCrypt hashing
     * @param plainPassword The plain text password to encrypt
     * @return The encrypted password hash
     */
    fun encryptPassword(plainPassword: String): String {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray())
    }
    
    /**
     * Validates a plain text password against an encrypted password hash
     * @param plainPassword The plain text password to validate
     * @param encryptedPassword The encrypted password hash to compare against
     * @return true if the password matches, false otherwise
     */
    fun validatePassword(plainPassword: String, encryptedPassword: String): Boolean {
        return try {
            val result = BCrypt.verifyer().verify(plainPassword.toCharArray(), encryptedPassword)
            result.verified
        } catch (e: Exception) {
            // If verification fails due to invalid hash format, return false
            false
        }
    }
    
    /**
     * Checks if a password string is already encrypted (BCrypt format)
     * BCrypt hashes start with $2a$, $2b$, $2x$, or $2y$
     * @param password The password string to check
     * @return true if the password appears to be encrypted, false otherwise
     */
    fun isPasswordEncrypted(password: String): Boolean {
        return password.startsWith("$2a$") || 
               password.startsWith("$2b$") || 
               password.startsWith("$2x$") || 
               password.startsWith("$2y$")
    }
}