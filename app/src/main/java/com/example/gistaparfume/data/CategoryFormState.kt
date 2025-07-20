package com.example.gistaparfume.data

/**
 * Data class representing the UI state for category add/edit forms
 * Manages form field values, validation states, and error messages
 */
data class CategoryFormState(
    /**
     * Category's title field value
     */
    val title: String = "",
    
    /**
     * Category's slug field value
     */
    val slug: String = "",
    
    /**
     * Validation error message for title field
     */
    val titleError: String? = null,
    
    /**
     * Validation error message for slug field
     */
    val slugError: String? = null,
    
    /**
     * Whether a save operation is currently in progress
     */
    val isLoading: Boolean = false,
    
    /**
     * General error message for form operations
     */
    val errorMessage: String? = null,
    
    /**
     * Whether the form is in edit mode (true) or add mode (false)
     */
    val isEditMode: Boolean = false,
    
    /**
     * Category ID when in edit mode
     */
    val categoryId: Int? = null,
    
    /**
     * Whether the last save/update operation was successful
     */
    val isOperationSuccessful: Boolean = false
) {
    /**
     * Computed property to check if the title field is valid
     */
    val isTitleValid: Boolean
        get() = title.isNotBlank() && titleError == null
    
    /**
     * Computed property to check if the slug field is valid
     */
    val isSlugValid: Boolean
        get() = slug.isNotBlank() && slugError == null
    
    /**
     * Computed property to check if the entire form is valid
     */
    val isFormValid: Boolean
        get() = isTitleValid && isSlugValid
    
    /**
     * Computed property to check if there are any validation errors
     */
    val hasErrors: Boolean
        get() = titleError != null || slugError != null || errorMessage != null
}