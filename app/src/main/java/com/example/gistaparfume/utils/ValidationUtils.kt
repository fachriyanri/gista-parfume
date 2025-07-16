package com.example.gistaparfume.utils

import java.util.regex.Pattern

/**
 * Utility object containing validation functions for user registration form
 */
object ValidationUtils {
    
    // Standard email regex pattern
    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )
    
    /**
     * Validates email format using regex pattern for standard email format
     * @param email The email string to validate
     * @return ValidationResult containing validation status and error message
     */
    fun validateEmail(email: String): ValidationResult {
        return if (email.isBlank()) {
            ValidationResult(isValid = false, errorMessage = null)
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            ValidationResult(isValid = false, errorMessage = "Email tidak sesuai format")
        } else {
            ValidationResult(isValid = true, errorMessage = null)
        }
    }
    
    /**
     * Validates password length for minimum 8 characters requirement
     * @param password The password string to validate
     * @return ValidationResult containing validation status and error message
     */
    fun validatePassword(password: String): ValidationResult {
        return if (password.length < 8) {
            ValidationResult(isValid = false, errorMessage = "Input password sesuai dengan kriteria")
        } else {
            ValidationResult(isValid = true, errorMessage = null)
        }
    }
    
    /**
     * Validates password confirmation matching with original password
     * @param password The original password
     * @param confirmPassword The confirmation password
     * @return ValidationResult containing validation status and error message
     */
    fun validatePasswordConfirmation(password: String, confirmPassword: String): ValidationResult {
        return if (confirmPassword.length < 8) {
            ValidationResult(isValid = false, errorMessage = "Input password sesuai dengan kriteria")
        } else if (password != confirmPassword) {
            ValidationResult(isValid = false, errorMessage = "Kedua sandi yang dimasukkan tidak sama")
        } else {
            ValidationResult(isValid = true, errorMessage = null)
        }
    }
}

/**
 * Data class to represent validation result
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)