package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.UserEntity

/**
 * Data class representing the UI state for the login screen
 * Manages loading states, authentication status, user information, and error handling
 */
data class LoginUiState(
    /**
     * Whether a login or password reset operation is currently in progress
     */
    val isLoading: Boolean = false,
    
    /**
     * Whether the user is currently logged in
     */
    val isLoggedIn: Boolean = false,
    
    /**
     * The currently authenticated user, null if not logged in
     */
    val currentUser: UserEntity? = null,
    
    /**
     * Error message to display to the user, null if no error
     */
    val errorMessage: String? = null,
    
    /**
     * Whether a password reset email has been successfully sent
     */
    val isPasswordResetSent: Boolean = false,
    
    /**
     * Whether the password reset operation is in progress
     */
    val isPasswordResetLoading: Boolean = false,
    
    /**
     * Error message specific to password reset operations
     */
    val passwordResetErrorMessage: String? = null
)