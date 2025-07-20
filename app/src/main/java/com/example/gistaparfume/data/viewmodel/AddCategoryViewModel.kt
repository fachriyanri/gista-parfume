package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.CategoryFormState
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.error.CategoryError
import com.example.gistaparfume.data.error.toCategoryError
import com.example.gistaparfume.data.repository.CategoryRepository
import com.example.gistaparfume.data.validation.CategoryValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCategoryViewModel(app: Application) : AndroidViewModel(app) {
    
    // Dependencies
    private val categoryRepository: CategoryRepository
    
    // Form State management using StateFlow
    private val _formState = MutableStateFlow(CategoryFormState())
    val formState: StateFlow<CategoryFormState> = _formState.asStateFlow()
    
    // Constants
    companion object {
        private const val TAG = "AddCategoryViewModel"
    }
    
    init {
        // Initialize repository
        val database = AppDatabaseConfig.getDatabase(app.applicationContext)
        categoryRepository = CategoryRepository(database.categoryDao())
    }
    
    /**
     * Updates the title field in the form state with comprehensive validation
     * Automatically generates slug from title to minimize user errors
     * @param title New title value
     */
    fun updateTitle(title: String) {
        val validationResult = CategoryValidator.validateTitle(title)
        val titleError = when (validationResult) {
            is CategoryValidator.ValidationResult.Error -> validationResult.error.message
            is CategoryValidator.ValidationResult.Success<*> -> null
        }
        
        // Auto-generate slug from title to minimize user input errors
        val generatedSlug = if (title.isNotBlank()) {
            CategoryValidator.generateSlugFromTitle(title)
        } else {
            ""
        }
        
        // Validate the generated slug
        val slugValidationResult = CategoryValidator.validateSlug(generatedSlug)
        val slugError = when (slugValidationResult) {
            is CategoryValidator.ValidationResult.Error -> slugValidationResult.error.message
            is CategoryValidator.ValidationResult.Success<*> -> null
        }
        
        _formState.value = _formState.value.copy(
            title = title, // Keep original input for UI display
            titleError = titleError,
            slug = generatedSlug, // Auto-generated slug
            slugError = slugError
        )
        
        Log.d(TAG, "Title updated: '$title', auto-generated slug: '$generatedSlug', title error: $titleError, slug error: $slugError")
    }
    
    /**
     * Updates the slug field in the form state with comprehensive validation
     * @param slug New slug value
     */
    fun updateSlug(slug: String) {
        val validationResult = CategoryValidator.validateSlug(slug)
        val slugError = when (validationResult) {
            is CategoryValidator.ValidationResult.Error -> validationResult.error.message
            is CategoryValidator.ValidationResult.Success<*> -> null
        }
        
        _formState.value = _formState.value.copy(
            slug = slug, // Keep original input for UI display
            slugError = slugError
        )
        
        Log.d(TAG, "Slug updated: '$slug', error: $slugError")
    }
    
    /**
     * Auto-generates slug from title
     * @param title Title to generate slug from
     */
    fun generateSlugFromTitle(title: String) {
        val generatedSlug = CategoryValidator.generateSlugFromTitle(title)
        updateSlug(generatedSlug)
        Log.d(TAG, "Generated slug from title '$title': '$generatedSlug'")
    }
    
    /**
     * Validates the entire form using comprehensive validation
     * @return True if form is valid, false otherwise
     */
    fun validateForm(): Boolean {
        val currentState = _formState.value
        val validationResult = CategoryValidator.validateCategory(currentState.title, currentState.slug)
        
        return when (validationResult) {
            is CategoryValidator.ValidationResult.Error -> {
                val error = validationResult.error
                when (error) {
                    is CategoryError.ValidationError -> {
                        when (error.field) {
                            "title" -> _formState.value = currentState.copy(titleError = error.message)
                            "slug" -> _formState.value = currentState.copy(slugError = error.message)
                            else -> _formState.value = currentState.copy(errorMessage = error.message)
                        }
                    }
                    else -> _formState.value = currentState.copy(errorMessage = error.message)
                }
                Log.d(TAG, "Form validation failed: ${error.message}")
                false
            }
            is CategoryValidator.ValidationResult.Success<*> -> {
                _formState.value = currentState.copy(
                    titleError = null,
                    slugError = null,
                    errorMessage = null
                )
                Log.d(TAG, "Form validation successful")
                true
            }
        }
    }
    
    /**
     * Saves the category to the database with comprehensive error handling
     * @return True if save was successful, false otherwise
     */
    fun saveCategory(): Boolean {
        val currentFormState = _formState.value
        
        // Validate form before saving
        if (!validateForm()) {
            _formState.value = currentFormState.copy(
                errorMessage = "Mohon lengkapi semua field dengan benar"
            )
            Log.w(TAG, "Save attempted with invalid form")
            return false
        }
        
        // Start loading state
        _formState.value = currentFormState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                val trimmedTitle = currentFormState.title.trim()
                val trimmedSlug = currentFormState.slug.trim()
                
                Log.d(TAG, "Attempting to save category: title='$trimmedTitle', slug='$trimmedSlug'")
                
                val result = categoryRepository.addCategory(trimmedTitle, trimmedSlug)
                
                result.fold(
                    onSuccess = { insertedId ->
                        Log.d(TAG, "Category saved successfully with ID: $insertedId")
                        _formState.value = currentFormState.copy(
                            isLoading = false,
                            errorMessage = null,
                            isOperationSuccessful = true
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to save category", exception)
                        
                        // Enhanced error handling with specific CategoryError types
                        val errorMessage = when (val categoryError = exception.toCategoryError()) {
                            is CategoryError.DuplicateError -> categoryError.message
                            is CategoryError.ValidationError -> categoryError.message
                            is CategoryError.DatabaseError -> categoryError.message
                            is CategoryError.NetworkError -> categoryError.message
                            is CategoryError.TimeoutError -> categoryError.message
                            is CategoryError.NotFoundError -> categoryError.message
                            is CategoryError.PermissionError -> categoryError.message
                            is CategoryError.ServerError -> categoryError.message
                            is CategoryError.UnknownError -> categoryError.message
                        }
                        
                        _formState.value = currentFormState.copy(
                            isLoading = false,
                            errorMessage = errorMessage
                        )
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during category save", e)
                val categoryError = e.toCategoryError()
                _formState.value = currentFormState.copy(
                    isLoading = false,
                    errorMessage = categoryError.message
                )
            }
        }
        
        return true
    }
    
    /**
     * Resets the form state to initial values
     */
    fun resetForm() {
        _formState.value = CategoryFormState()
        Log.d(TAG, "Form reset to initial state")
    }
    
    /**
     * Clears form error messages
     */
    fun clearFormError() {
        _formState.value = _formState.value.copy(errorMessage = null)
        Log.d(TAG, "Form error cleared")
    }
    
    /**
     * Clears the operation success flag
     */
    fun clearOperationSuccess() {
        _formState.value = _formState.value.copy(isOperationSuccessful = false)
        Log.d(TAG, "Operation success flag cleared")
    }
    
    /**
     * Resets form after successful operation
     */
    fun resetFormAfterSuccess() {
        _formState.value = CategoryFormState()
        Log.d(TAG, "Form reset after successful operation")
    }
}