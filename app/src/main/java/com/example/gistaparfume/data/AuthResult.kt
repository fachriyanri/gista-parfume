package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.UserEntity

/**
 * Sealed class representing the result of authentication operations
 */
sealed class AuthResult {
    /**
     * Authentication was successful
     * @param user The authenticated user entity
     */
    data class Success(val user: UserEntity) : AuthResult()
    
    /**
     * Authentication failed with a general error
     * @param message The error message to display
     */
    data class Error(val message: String) : AuthResult()
    
    /**
     * Authentication failed because the email was not found in the database
     */
    object EmailNotFound : AuthResult()
    
    /**
     * Authentication failed because the password was invalid
     */
    object InvalidPassword : AuthResult()
}