package com.example.gistaparfume.utils

import com.example.gistaparfume.data.UserFormState
import com.example.gistaparfume.data.entity.UserRole

/**
 * Utility class for validating user form data
 * Integrates all validation functions and provides comprehensive form validation
 */
object UserFormValidator {
    
    /**
     * Validates the entire user form and returns updated form state with validation errors
     * @param formState Current form state to validate
     * @return Updated UserFormState with validation errors set
     */
    fun validateForm(formState: UserFormState): UserFormState {
        val nameValidation = ValidationUtils.validateName(formState.name)
        val emailValidation = ValidationUtils.validateEmail(formState.email)
        val passwordValidation = ValidationUtils.validatePassword(formState.password)
        val roleValidation = ValidationUtils.validateRole(formState.role)
        val statusValidation = ValidationUtils.validateStatus(formState.isActive)
        
        return formState.copy(
            nameError = if (!nameValidation.isValid) nameValidation.errorMessage else null,
            emailError = if (!emailValidation.isValid) emailValidation.errorMessage else null,
            passwordError = if (!passwordValidation.isValid) passwordValidation.errorMessage else null,
            roleError = if (!roleValidation.isValid) roleValidation.errorMessage else null,
            statusError = if (!statusValidation.isValid) statusValidation.errorMessage else null
        )
    }
    
    /**
     * Validates only the name field
     * @param formState Current form state
     * @return Updated UserFormState with name validation error
     */
    fun validateName(formState: UserFormState): UserFormState {
        val nameValidation = ValidationUtils.validateName(formState.name)
        return formState.copy(
            nameError = if (!nameValidation.isValid) nameValidation.errorMessage else null
        )
    }
    
    /**
     * Validates only the email field
     * @param formState Current form state
     * @return Updated UserFormState with email validation error
     */
    fun validateEmail(formState: UserFormState): UserFormState {
        val emailValidation = ValidationUtils.validateEmail(formState.email)
        return formState.copy(
            emailError = if (!emailValidation.isValid) emailValidation.errorMessage else null
        )
    }
    
    /**
     * Validates only the password field
     * @param formState Current form state
     * @return Updated UserFormState with password validation error
     */
    fun validatePassword(formState: UserFormState): UserFormState {
        val passwordValidation = ValidationUtils.validatePassword(formState.password)
        return formState.copy(
            passwordError = if (!passwordValidation.isValid) passwordValidation.errorMessage else null
        )
    }
    
    /**
     * Validates only the role selection
     * @param formState Current form state
     * @return Updated UserFormState with role validation error
     */
    fun validateRole(formState: UserFormState): UserFormState {
        val roleValidation = ValidationUtils.validateRole(formState.role)
        return formState.copy(
            roleError = if (!roleValidation.isValid) roleValidation.errorMessage else null
        )
    }
    
    /**
     * Validates only the status selection
     * @param formState Current form state
     * @return Updated UserFormState with status validation error
     */
    fun validateStatus(formState: UserFormState): UserFormState {
        val statusValidation = ValidationUtils.validateStatus(formState.isActive)
        return formState.copy(
            statusError = if (!statusValidation.isValid) statusValidation.errorMessage else null
        )
    }
    
    /**
     * Clears all validation errors from the form state
     * @param formState Current form state
     * @return Updated UserFormState with all validation errors cleared
     */
    fun clearValidationErrors(formState: UserFormState): UserFormState {
        return formState.copy(
            nameError = null,
            emailError = null,
            passwordError = null,
            roleError = null,
            statusError = null,
            errorMessage = null
        )
    }
    
    /**
     * Checks if the form has any validation errors
     * @param formState Current form state
     * @return True if form has validation errors, false otherwise
     */
    fun hasValidationErrors(formState: UserFormState): Boolean {
        return formState.hasErrors
    }
    
    /**
     * Checks if the form is valid and ready for submission
     * @param formState Current form state
     * @return True if form is valid, false otherwise
     */
    fun isFormValid(formState: UserFormState): Boolean {
        return formState.isFormValid
    }
}