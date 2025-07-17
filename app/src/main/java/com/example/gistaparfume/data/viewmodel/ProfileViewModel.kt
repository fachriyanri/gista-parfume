package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.repository.UserRepository
import com.example.gistaparfume.utils.ImageUtils
import com.example.gistaparfume.utils.ImageValidationUtils
import com.example.gistaparfume.utils.PasswordUtils
import com.example.gistaparfume.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing profile viewing and editing functionality
 * Handles profile data loading, form state management, and profile updates
 */
class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    
    // Dependencies
    private val userRepository: UserRepository = UserRepository.getInstance(app.applicationContext)
    
    // UI State management using StateFlow
    private val _profileUiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()
    
    private val _formState = MutableStateFlow(ProfileFormState())
    val formState: StateFlow<ProfileFormState> = _formState.asStateFlow()
    
    companion object {
        private const val TAG = "ProfileViewModel"
        private const val MAX_PASSWORD_LENGTH = 8
    }
    
    /**
     * Loads user profile data by user ID
     * @param userId ID of the user to load profile for
     */
    fun loadUserProfile(userId: Int) {
        viewModelScope.launch {
            _profileUiState.value = _profileUiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _profileUiState.value = _profileUiState.value.copy(
                        user = user,
                        isLoading = false,
                        errorMessage = null
                    )
                    
                    // Pre-populate form state with current user data
                    _formState.value = _formState.value.copy(
                        name = user.name,
                        email = user.email,
                        password = "", // Don't pre-fill password for security
                        imageUri = user.image
                    )
                    
                    Log.d(TAG, "Profile loaded successfully for user: ${user.id}")
                } else {
                    _profileUiState.value = _profileUiState.value.copy(
                        isLoading = false,
                        errorMessage = "Profil pengguna tidak ditemukan"
                    )
                    Log.w(TAG, "User not found with ID: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user profile", e)
                _profileUiState.value = _profileUiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat profil pengguna. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Updates the name field in the form state with validation
     * @param name New name value
     */
    fun updateFormName(name: String) {
        val validationResult = ValidationUtils.validateName(name)
        
        _formState.value = _formState.value.copy(
            name = name,
            nameError = if (!validationResult.isValid) validationResult.errorMessage else null
        )
    }
    
    /**
     * Updates the email field in the form state with validation
     * @param email New email value
     */
    fun updateFormEmail(email: String) {
        val validationResult = ValidationUtils.validateEmail(email)
        
        _formState.value = _formState.value.copy(
            email = email,
            emailError = if (!validationResult.isValid) validationResult.errorMessage else null
        )
    }
    
    /**
     * Updates the password field in the form state with custom validation
     * Profile password has maximum 8 characters requirement (different from registration)
     * @param password New password value
     */
    fun updateFormPassword(password: String) {
        val passwordError = when {
            password.isNotEmpty() && password.length < MAX_PASSWORD_LENGTH ->
                "Password maksimal $MAX_PASSWORD_LENGTH karakter"
            else -> null
        }
        
        _formState.value = _formState.value.copy(
            password = password,
            passwordError = passwordError
        )
    }
    
    /**
     * Updates the image URI in the form state with validation and processing
     * @param imageUri New image URI value (content:// URI from gallery picker)
     */
    fun updateFormImage(imageUri: String?) {
        viewModelScope.launch {
            try {
                if (imageUri != null) {
                    // Validate the image first
                    val validationResult = ImageValidationUtils.validateImageForForm(
                        getApplication<Application>().applicationContext,
                        imageUri,
                        isRequired = false
                    )
                    
                    if (validationResult.isValid) {
                        // Store the content URI temporarily - will be processed during save
                        _formState.value = _formState.value.copy(
                            imageUri = imageUri,
                            imageError = null
                        )
                        Log.d(TAG, "Image selected and validated: $imageUri")
                    } else {
                        _formState.value = _formState.value.copy(
                            imageError = validationResult.errorMessage
                        )
                        Log.w(TAG, "Image validation failed: ${validationResult.errorMessage}")
                    }
                } else {
                    // Clear image selection
                    _formState.value = _formState.value.copy(
                        imageUri = null,
                        imageError = null
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating form image", e)
                _formState.value = _formState.value.copy(
                    imageError = "Gagal memproses gambar yang dipilih"
                )
            }
        }
    }
    
    /**
     * Toggles password visibility in the form
     */
    fun togglePasswordVisibility() {
        _formState.value = _formState.value.copy(
            isPasswordVisible = !_formState.value.isPasswordVisible
        )
    }
    
    /**
     * Saves profile changes to the database
     * Updates user profile with form data including password encryption
     */
    fun saveProfileChanges() {
        val currentFormState = _formState.value
        val currentUser = _profileUiState.value.user
        
        if (currentUser == null) {
            _formState.value = currentFormState.copy(
                errorMessage = "Data pengguna tidak tersedia"
            )
            return
        }
        
        // Validate form before saving
        if (!currentFormState.isFormValid) {
            _formState.value = currentFormState.copy(
                errorMessage = "Mohon lengkapi semua field dengan benar"
            )
            return
        }
        
        viewModelScope.launch {
            _formState.value = currentFormState.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                // Determine final password - encrypt if changed, keep existing if empty
                val finalPassword = if (currentFormState.password.isNotEmpty()) {
                    PasswordUtils.encryptPassword(currentFormState.password)
                } else {
                    currentUser.password // Keep existing password
                }
                
                // Process image if a new one was selected
                val finalImagePath = processImageForSaving(
                    currentFormState.imageUri,
                    currentUser.image,
                    currentUser.id
                )
                
                // Create updated user entity
                val updatedUser = currentUser.copy(
                    name = currentFormState.name,
                    email = currentFormState.email,
                    password = finalPassword,
                    image = finalImagePath
                )
                
                // Update user in repository
                val result = userRepository.updateUser(updatedUser)
                
                result.fold(
                    onSuccess = {
                        Log.d(TAG, "Profile updated successfully for user: ${currentUser.id}")
                        
                        // Update profile UI state with new data
                        _profileUiState.value = _profileUiState.value.copy(
                            user = updatedUser
                        )
                        
                        _formState.value = currentFormState.copy(
                            isLoading = false,
                            errorMessage = null,
                            isUpdateSuccessful = true
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to update profile", exception)
                        _formState.value = currentFormState.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Gagal memperbarui profil. Silakan coba lagi."
                        )
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during profile update", e)
                _formState.value = currentFormState.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Resets the form state to initial values
     */
    fun resetForm() {
        val currentUser = _profileUiState.value.user
        _formState.value = ProfileFormState(
            name = currentUser?.name ?: "",
            email = currentUser?.email ?: "",
            password = "",
            imageUri = currentUser?.image
        )
    }
    
    /**
     * Clears form error messages
     */
    fun clearErrors() {
        _formState.value = _formState.value.copy(
            nameError = null,
            emailError = null,
            passwordError = null,
            imageError = null,
            errorMessage = null
        )
    }
    
    /**
     * Clears the update success flag
     */
    fun clearUpdateSuccess() {
        _formState.value = _formState.value.copy(
            isUpdateSuccessful = false
        )
    }
    
    /**
     * Clears profile error messages
     */
    fun clearProfileError() {
        _profileUiState.value = _profileUiState.value.copy(
            errorMessage = null
        )
    }
    
    /**
     * Processes image for saving - handles content URIs and file paths
     * @param newImageUri New image URI from form (could be content:// or file path)
     * @param currentImagePath Current user's image path (file path or null)
     * @param userId User ID for organizing saved images
     * @return Final image path to store in database
     */
    private suspend fun processImageForSaving(
        newImageUri: String?,
        currentImagePath: String?,
        userId: Int
    ): String? {
        return try {
            when {
                // No image selected - keep current image
                newImageUri == null -> currentImagePath
                
                // Same image as current - no change needed
                newImageUri == currentImagePath -> currentImagePath
                
                // New content URI from gallery picker - process and save
                newImageUri.startsWith("content://") -> {
                    val uri = newImageUri.toUri()
                    val context = getApplication<Application>().applicationContext
                    
                    // Delete old image if it exists
                    if (currentImagePath != null && currentImagePath != newImageUri) {
                        ImageUtils.deleteImage(currentImagePath)
                    }
                    
                    // Save and process new image
                    val result = ImageUtils.saveImageToInternalStorage(context, uri, userId)
                    
                    if (result.success) {
                        Log.d(TAG, "Image processed and saved: ${result.filePath}")
                        result.filePath
                    } else {
                        Log.e(TAG, "Failed to process image: ${result.errorMessage}")
                        throw Exception(result.errorMessage ?: "Gagal memproses gambar")
                    }
                }
                
                // Already a processed file path - validate it exists
                else -> {
                    if (ImageUtils.imageExists(newImageUri)) {
                        newImageUri
                    } else {
                        Log.w(TAG, "Image file not found: $newImageUri")
                        currentImagePath // Fall back to current image
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image for saving", e)
            throw e // Re-throw to be handled by saveProfileChanges
        }
    }
}

/**
 * Data class representing the UI state for profile viewing
 */
data class ProfileUiState(
    val user: UserEntity? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Data class representing the form state for profile editing
 */
data class ProfileFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val imageUri: String? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val imageError: String? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isUpdateSuccessful: Boolean = false,
    val errorMessage: String? = null
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
     * Password is optional in profile editing (empty means no change)
     */
    val isPasswordValid: Boolean
        get() = passwordError == null
    
    /**
     * Computed property to check if the image field is valid
     */
    val isImageValid: Boolean
        get() = imageError == null
    
    /**
     * Computed property to check if the entire form is valid
     */
    val isFormValid: Boolean
        get() = isNameValid && isEmailValid && isPasswordValid && isImageValid
    
    /**
     * Computed property to check if there are any validation errors
     */
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null || imageError != null || errorMessage != null
}