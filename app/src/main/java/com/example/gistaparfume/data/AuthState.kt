package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole

/**
 * Data class representing the current authentication state of the application
 */
data class AuthState(
    /**
     * Whether the user is currently authenticated
     */
    val isAuthenticated: Boolean = false,
    
    /**
     * The currently authenticated user, null if not authenticated
     */
    val currentUser: UserEntity? = null,
    
    /**
     * The role of the current user, null if not authenticated
     */
    val userRole: UserRole? = null
)