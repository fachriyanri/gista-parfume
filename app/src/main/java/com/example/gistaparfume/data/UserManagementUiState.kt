package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.UserEntity

/**
 * Data class representing the UI state for the user management screen
 * Manages user list display, pagination, search functionality, and loading states
 */
data class UserManagementUiState(
    /**
     * List of users to display in the current page
     */
    val users: List<UserEntity> = emptyList(),
    
    /**
     * Current page number (1-based)
     */
    val currentPage: Int = 1,
    
    /**
     * Total number of pages available
     */
    val totalPages: Int = 1,
    
    /**
     * Whether a data loading operation is currently in progress
     */
    val isLoading: Boolean = false,
    
    /**
     * Current search query string
     */
    val searchQuery: String = "",
    
    /**
     * Error message to display to the user, null if no error
     */
    val errorMessage: String? = null,
    
    /**
     * Total number of users (for pagination calculation)
     */
    val totalUsers: Int = 0,
    
    /**
     * Whether a delete operation is currently in progress
     */
    val isDeleting: Boolean = false
) {
    /**
     * Computed property to check if there are users to display
     */
    val hasUsers: Boolean
        get() = users.isNotEmpty()
    
    /**
     * Computed property to check if search is active
     */
    val isSearchActive: Boolean
        get() = searchQuery.isNotBlank()
    
    /**
     * Computed property to check if pagination controls should be shown
     */
    val shouldShowPagination: Boolean
        get() = totalPages > 1
}