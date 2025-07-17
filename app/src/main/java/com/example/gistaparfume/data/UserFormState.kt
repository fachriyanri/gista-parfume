package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.UserRole

/**
 * Data class representing the UI state for user add/edit forms
 * Manages form field values, validation states, and error messages
 */
data class UserFormState(
    /**
     * User's name field value
     */
    val name: String = "",
    
    /**
     * User's email field value
     */
    val email: String = "",
    
    /**
     * User's password field value
     */
    val password: String = "",
    
    /**
     * User's selected role
     */
    val role: UserRole = UserRole.MEMBER,
    
    /**
     * User's active status
     */
    val isActive: Boolean = true,
    
    /**
     * Selected image URI for user profile photo
     */
    val imageUri: String? = null,
    
    /**
     * Validation error message for name field
     */
    val nameError: String? = null,
    
    /**
     * Validation error message for email field
     */
    val emailError: String? = null,
    
    /**
     * Validation error message for password field
     */
    val passwordError: String? = null,
    
    /**
     * Validation error message for role selection
     */
    val roleError: String? = null,
    
    /**
     * Validation error message for status selection
     */
    val statusError: String? = null,
    
    /**
     * Validation error message for image selection
     */
    val imageError: String? = null,
    
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
     * User ID when in edit mode
     */
    val userId: Int? = null,
    
    /**
     * Whether the last save/update operation was successful
     */
    val isOperationSuccessful: Boolean = false
) {
    /**
     * Computed property to check if the name field is valid
     */
    val isNameValid: Boolean
        get() = name.isNotBlank() && nameError == null
    
    /**
     * Computed property to check if the email field is valid
     */
    val isEmailValid: Boolean
        get() = email.isNotBlank() && emailError == null
    
    /**
     * Computed property to check if the password field is valid
     */
    val isPasswordValid: Boolean
        get() = if (isEditMode) {
            // In edit mode, password can be blank (means no change) or valid
            password.isBlank() || (password.isNotBlank() && passwordError == null)
        } else {
            // In add mode, password must be provided and valid
            password.isNotBlank() && passwordError == null
        }
    
    /**
     * Computed property to check if role selection is valid
     */
    val isRoleValid: Boolean
        get() = roleError == null
    
    /**
     * Computed property to check if status selection is valid
     */
    val isStatusValid: Boolean
        get() = statusError == null
    
    /**
     * Computed property to check if image selection is valid
     */
    val isImageValid: Boolean
        get() = imageError == null
    
    /**
     * Computed property to check if the entire form is valid
     */
    val isFormValid: Boolean
        get() = isNameValid && isEmailValid && isPasswordValid && isRoleValid && isStatusValid && isImageValid
    
    /**
     * Computed property to check if there are any validation errors
     */
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null || 
                roleError != null || statusError != null || imageError != null || errorMessage != null
}