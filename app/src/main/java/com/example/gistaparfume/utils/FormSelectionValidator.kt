package com.example.gistaparfume.utils

import com.example.gistaparfume.data.entity.UserRole

/**
 * Utility class for validating form selections to prevent multiple selections
 * Specifically handles role and status selection validation for radio button groups
 */
object FormSelectionValidator {
    
    /**
     * Validates role selection to ensure only one role is selected at a time
     * This is used by radio button groups to prevent multiple selections
     * @param selectedRole The currently selected role
     * @param newRole The role being selected
     * @return ValidationResult indicating if the selection is valid
     */
    fun validateRoleSelection(selectedRole: UserRole?, newRole: UserRole): ValidationResult {
        // If no role is currently selected, allow the new selection
        if (selectedRole == null) {
            return ValidationResult(isValid = true, errorMessage = null)
        }
        
        // If the same role is selected again, it's valid (deselection or confirmation)
        if (selectedRole == newRole) {
            return ValidationResult(isValid = true, errorMessage = null)
        }
        
        // If a different role is being selected, it's valid (changing selection)
        return ValidationResult(isValid = true, errorMessage = null)
    }
    
    /**
     * Validates status selection to ensure only one status is selected at a time
     * This is used by radio button groups to prevent multiple selections
     * @param selectedStatus The currently selected status
     * @param newStatus The status being selected
     * @return ValidationResult indicating if the selection is valid
     */
    fun validateStatusSelection(selectedStatus: Boolean?, newStatus: Boolean): ValidationResult {
        // If no status is currently selected, allow the new selection
        if (selectedStatus == null) {
            return ValidationResult(isValid = true, errorMessage = null)
        }
        
        // If the same status is selected again, it's valid (deselection or confirmation)
        if (selectedStatus == newStatus) {
            return ValidationResult(isValid = true, errorMessage = null)
        }
        
        // If a different status is being selected, it's valid (changing selection)
        return ValidationResult(isValid = true, errorMessage = null)
    }
    
    /**
     * Validates that a role has been selected (not null)
     * @param role The selected role
     * @return ValidationResult indicating if a role is selected
     */
    fun validateRoleRequired(role: UserRole?): ValidationResult {
        return if (role == null) {
            ValidationResult(isValid = false, errorMessage = "Role harus dipilih")
        } else {
            ValidationResult(isValid = true, errorMessage = null)
        }
    }
    
    /**
     * Validates that a status has been selected (not null)
     * @param status The selected status
     * @return ValidationResult indicating if a status is selected
     */
    fun validateStatusRequired(status: Boolean?): ValidationResult {
        return if (status == null) {
            ValidationResult(isValid = false, errorMessage = "Status harus dipilih")
        } else {
            ValidationResult(isValid = true, errorMessage = null)
        }
    }
    
    /**
     * Ensures only one role can be selected in a radio button group
     * @param currentSelections List of currently selected roles (should be max 1)
     * @param newSelection The role being selected
     * @return Pair of ValidationResult and the corrected selection list
     */
    fun enforceRoleSingleSelection(
        currentSelections: List<UserRole>, 
        newSelection: UserRole
    ): Pair<ValidationResult, List<UserRole>> {
        // If multiple roles are somehow selected, this is invalid
        if (currentSelections.size > 1) {
            return Pair(
                ValidationResult(isValid = false, errorMessage = "Hanya satu role yang dapat dipilih"),
                listOf(newSelection) // Force single selection
            )
        }
        
        // If the new selection is already in the list, remove it (toggle off)
        if (currentSelections.contains(newSelection)) {
            return Pair(
                ValidationResult(isValid = true, errorMessage = null),
                emptyList()
            )
        }
        
        // Replace any existing selection with the new one
        return Pair(
            ValidationResult(isValid = true, errorMessage = null),
            listOf(newSelection)
        )
    }
    
    /**
     * Ensures only one status can be selected in a radio button group
     * @param currentSelections List of currently selected statuses (should be max 1)
     * @param newSelection The status being selected
     * @return Pair of ValidationResult and the corrected selection list
     */
    fun enforceStatusSingleSelection(
        currentSelections: List<Boolean>, 
        newSelection: Boolean
    ): Pair<ValidationResult, List<Boolean>> {
        // If multiple statuses are somehow selected, this is invalid
        if (currentSelections.size > 1) {
            return Pair(
                ValidationResult(isValid = false, errorMessage = "Hanya satu status yang dapat dipilih"),
                listOf(newSelection) // Force single selection
            )
        }
        
        // If the new selection is already in the list, remove it (toggle off)
        if (currentSelections.contains(newSelection)) {
            return Pair(
                ValidationResult(isValid = true, errorMessage = null),
                emptyList()
            )
        }
        
        // Replace any existing selection with the new one
        return Pair(
            ValidationResult(isValid = true, errorMessage = null),
            listOf(newSelection)
        )
    }
}