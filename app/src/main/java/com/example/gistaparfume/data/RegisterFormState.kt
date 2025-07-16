package com.example.gistaparfume.data

/**
 * Data class to manage all form validation states for user registration
 */
data class RegisterFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
    val isPasswordMatch: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    /**
     * Computed property to check if the entire form is valid
     */
    val isFormValid: Boolean
        get() = name.isNotBlank() && isEmailValid && isPasswordValid && isPasswordMatch
}