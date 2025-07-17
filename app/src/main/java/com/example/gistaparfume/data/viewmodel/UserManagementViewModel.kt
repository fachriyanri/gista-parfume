package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.UserManagementUiState
import com.example.gistaparfume.data.UserFormState
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import com.example.gistaparfume.data.repository.UserRepository
import com.example.gistaparfume.utils.ValidationUtils
import com.example.gistaparfume.utils.PasswordUtils
import com.example.gistaparfume.utils.ImageUtils
import com.example.gistaparfume.utils.ImageValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(app: Application) : AndroidViewModel(app) {
    
    // Dependencies
    private val userRepository: UserRepository = UserRepository.getInstance(app.applicationContext)
    
    // UI State management using StateFlow
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()
    
    // Form State management for Add/Edit User
    private val _formState = MutableStateFlow(UserFormState())
    val formState: StateFlow<UserFormState> = _formState.asStateFlow()
    
    // Constants
    companion object {
        private const val PAGE_SIZE = 5
        private const val TAG = "UserManagementViewModel"
    }
    
    init {
        // Load initial user data
        loadUsers()
    }
    
    /**
     * Loads users for the current page
     * Handles both regular loading and search-based loading
     */
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val currentState = _uiState.value
                val users: List<UserEntity>
                val totalCount: Int
                
                if (currentState.isSearchActive) {
                    // Load search results
                    users = userRepository.searchUsers(
                        query = currentState.searchQuery,
                        page = currentState.currentPage,
                        pageSize = PAGE_SIZE
                    )
                    totalCount = userRepository.getSearchResultCount(currentState.searchQuery)
                } else {
                    // Load all users
                    users = userRepository.getAllUsers(
                        page = currentState.currentPage,
                        pageSize = PAGE_SIZE
                    )
                    totalCount = userRepository.getTotalUserCount()
                }
                
                val totalPages = if (totalCount > 0) {
                    (totalCount + PAGE_SIZE - 1) / PAGE_SIZE
                } else {
                    1
                }
                
                _uiState.value = _uiState.value.copy(
                    users = users,
                    totalUsers = totalCount,
                    totalPages = totalPages,
                    isLoading = false,
                    errorMessage = null
                )
                
                Log.d(TAG, "Loaded ${users.size} users for page ${currentState.currentPage}")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading users", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat data pengguna. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Navigates to the next page
     */
    fun nextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < currentState.totalPages) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage + 1
            )
            loadUsers()
        }
    }
    
    /**
     * Navigates to the previous page
     */
    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 1) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage - 1
            )
            loadUsers()
        }
    }
    
    /**
     * Navigates to a specific page
     * @param page Page number (1-based)
     */
    fun goToPage(page: Int) {
        val currentState = _uiState.value
        if (page in 1..currentState.totalPages && page != currentState.currentPage) {
            _uiState.value = currentState.copy(
                currentPage = page
            )
            loadUsers()
        }
    }
    
    /**
     * Performs search with the given query
     * @param query Search query to match against name or email
     */
    fun searchUsers(query: String) {
        val trimmedQuery = query.trim()
        
        _uiState.value = _uiState.value.copy(
            searchQuery = trimmedQuery,
            currentPage = 1 // Reset to first page when searching
        )
        
        loadUsers()
        
        Log.d(TAG, "Searching users with query: '$trimmedQuery'")
    }
    
    /**
     * Clears the search query and reloads all users
     */
    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            currentPage = 1 // Reset to first page when clearing search
        )
        
        loadUsers()
        
        Log.d(TAG, "Search cleared, loading all users")
    }
    
    /**
     * Deletes a user by ID
     * @param userId ID of the user to delete
     */
    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                errorMessage = null
            )
            
            try {
                // Get user data before deletion to clean up image
                val userToDelete = userRepository.getUserById(userId)
                
                val result = userRepository.deleteUser(userId)
                
                result.fold(
                    onSuccess = {
                        Log.d(TAG, "User deleted successfully: $userId")
                        
                        // Clean up user's image file if it exists
                        userToDelete?.image?.let { imagePath ->
                            viewModelScope.launch {
                                val deleted = ImageUtils.deleteImage(imagePath)
                                if (deleted) {
                                    Log.d(TAG, "User image deleted: $imagePath")
                                } else {
                                    Log.w(TAG, "Failed to delete user image: $imagePath")
                                }
                            }
                        }
                        
                        // Check if current page becomes empty after deletion
                        val currentState = _uiState.value
                        val newTotalCount = currentState.totalUsers - 1
                        val newTotalPages = if (newTotalCount > 0) {
                            (newTotalCount + PAGE_SIZE - 1) / PAGE_SIZE
                        } else {
                            1
                        }
                        
                        // If current page is now beyond total pages, go to previous page
                        val newCurrentPage = if (currentState.currentPage > newTotalPages) {
                            maxOf(1, newTotalPages)
                        } else {
                            currentState.currentPage
                        }
                        
                        _uiState.value = currentState.copy(
                            currentPage = newCurrentPage,
                            isDeleting = false
                        )
                        
                        // Reload users to reflect the deletion
                        loadUsers()
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to delete user: $userId", exception)
                        _uiState.value = _uiState.value.copy(
                            isDeleting = false,
                            errorMessage = exception.message ?: "Gagal menghapus pengguna. Silakan coba lagi."
                        )
                    }
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during user deletion", e)
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Refreshes the user list
     * Useful for pull-to-refresh functionality
     */
    fun refreshUsers() {
        loadUsers()
        Log.d(TAG, "User list refreshed")
    }
    
    /**
     * Clears error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Gets the display number for a user in the list
     * Implements continuous numbering across pages
     * @param index Index of the user in the current page list
     * @return Display number for the user
     */
    fun getUserDisplayNumber(index: Int): Int {
        val currentState = _uiState.value
        return (currentState.currentPage - 1) * PAGE_SIZE + index + 1
    }
    
    /**
     * Gets the role display text for a user
     * @param user UserEntity to get role display for
     * @return String representation of the role
     */
    fun getRoleDisplayText(user: UserEntity): String {
        return when (user.role) {
            com.example.gistaparfume.data.entity.UserRole.ADMIN -> "admin"
            com.example.gistaparfume.data.entity.UserRole.MEMBER -> "member"
        }
    }
    
    /**
     * Gets the status display text for a user
     * @param user UserEntity to get status display for
     * @return String representation of the status
     */
    fun getStatusDisplayText(user: UserEntity): String {
        return if (user.isActive) "Aktif" else "Tidak Aktif"
    }
    
    /**
     * Checks if there are more pages available for pagination
     * @return Boolean indicating if next page is available
     */
    fun hasNextPage(): Boolean {
        val currentState = _uiState.value
        return currentState.currentPage < currentState.totalPages
    }
    
    /**
     * Checks if there are previous pages available for pagination
     * @return Boolean indicating if previous page is available
     */
    fun hasPreviousPage(): Boolean {
        val currentState = _uiState.value
        return currentState.currentPage > 1
    }
    
    /**
     * Gets pagination info text
     * @return String showing current pagination status
     */
    fun getPaginationInfo(): String {
        val currentState = _uiState.value
        val startItem = if (currentState.users.isNotEmpty()) {
            (currentState.currentPage - 1) * PAGE_SIZE + 1
        } else {
            0
        }
        val endItem = minOf(
            currentState.currentPage * PAGE_SIZE,
            currentState.totalUsers
        )
        
        return "Menampilkan $startItem-$endItem dari ${currentState.totalUsers} pengguna"
    }
    
    // ===== FORM MANAGEMENT METHODS =====
    
    /**
     * Updates the name field in the form state
     * @param name New name value
     */
    fun updateFormName(name: String) {
        val nameError = if (name.isBlank()) "Nama tidak boleh kosong" else null
        
        _formState.value = _formState.value.copy(
            name = name,
            nameError = nameError
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
     * Updates the password field in the form state with validation
     * @param password New password value
     */
    fun updateFormPassword(password: String) {
        val currentFormState = _formState.value
        
        // In edit mode, allow blank password (means no change)
        val passwordError = if (currentFormState.isEditMode && password.isBlank()) {
            null // Blank password is allowed in edit mode
        } else {
            val validationResult = ValidationUtils.validatePassword(password)
            if (!validationResult.isValid) validationResult.errorMessage else null
        }
        
        _formState.value = _formState.value.copy(
            password = password,
            passwordError = passwordError
        )
    }
    
    /**
     * Updates the role field in the form state
     * @param role New role value
     */
    fun updateFormRole(role: UserRole) {
        _formState.value = _formState.value.copy(
            role = role,
            roleError = null // Clear any previous role error
        )
    }
    
    /**
     * Updates the status field in the form state
     * @param isActive New status value
     */
    fun updateFormStatus(isActive: Boolean) {
        _formState.value = _formState.value.copy(
            isActive = isActive,
            statusError = null // Clear any previous status error
        )
    }
    
    /**
     * Updates the image URI in the form state with validation and processing
     * @param imageUri New image URI value
     */
    fun updateFormImage(imageUri: String?) {
        if (imageUri != null) {
            // Quick validation first for immediate UI feedback
            val quickValidation = ImageValidationUtils.validateImageUriQuick(imageUri, false)
            
            if (!quickValidation.isValid) {
                _formState.value = _formState.value.copy(
                    imageUri = null,
                    imageError = quickValidation.errorMessage
                )
                return
            }
            
            // Async validation for detailed checks
            viewModelScope.launch {
                try {
                    val validationResult = ImageValidationUtils.validateImageForForm(
                        getApplication(), 
                        imageUri, 
                        false
                    )
                    
                    if (validationResult.isValid) {
                        _formState.value = _formState.value.copy(
                            imageUri = imageUri,
                            imageError = null
                        )
                    } else {
                        _formState.value = _formState.value.copy(
                            imageUri = null,
                            imageError = validationResult.errorMessage
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error validating image", e)
                    _formState.value = _formState.value.copy(
                        imageUri = null,
                        imageError = "Gagal memvalidasi gambar"
                    )
                }
            }
        } else {
            _formState.value = _formState.value.copy(
                imageUri = null,
                imageError = null
            )
        }
    }
    
    /**
     * Processes and saves an image from URI to internal storage
     * @param imageUri URI of the image to process and save
     */
    fun processAndSaveImage(imageUri: String?) {
        if (imageUri == null) {
            updateFormImage(null)
            return
        }
        
        viewModelScope.launch {
            try {
                val uri = Uri.parse(imageUri)
                val userId = _formState.value.userId
                
                val processingResult = ImageUtils.saveImageToInternalStorage(
                    getApplication(),
                    uri,
                    userId
                )
                
                if (processingResult.success) {
                    _formState.value = _formState.value.copy(
                        imageUri = processingResult.filePath,
                        errorMessage = null
                    )
                    Log.d(TAG, "Image processed and saved: ${processingResult.filePath}")
                } else {
                    _formState.value = _formState.value.copy(
                        imageUri = null,
                        errorMessage = processingResult.errorMessage
                    )
                    Log.e(TAG, "Failed to process image: ${processingResult.errorMessage}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image", e)
                _formState.value = _formState.value.copy(
                    imageUri = null,
                    errorMessage = "Gagal memproses gambar"
                )
            }
        }
    }
    
    /**
     * Resets the form state to initial values
     */
    fun resetForm() {
        _formState.value = UserFormState()
    }
    
    /**
     * Resets form after successful operation and clears success state
     */
    fun resetFormAfterSuccessfulOperation() {
        _formState.value = UserFormState()
    }
    
    /**
     * Loads user data into the form for editing
     * @param userId ID of the user to edit
     */
    fun loadUserForEdit(userId: Int) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _formState.value = UserFormState(
                        name = user.name,
                        email = user.email,
                        password = "", // Don't pre-fill password for security
                        role = user.role,
                        isActive = user.isActive,
                        imageUri = user.image,
                        isEditMode = true,
                        userId = user.id,
                        isLoading = false
                    )
                } else {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        errorMessage = "Pengguna tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user for edit", e)
                _formState.value = _formState.value.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat data pengguna"
                )
            }
        }
    }
    
    /**
     * Saves the user (create or update based on form mode)
     */
    fun saveUser() {
        val currentFormState = _formState.value
        
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
                val encryptedPassword = PasswordUtils.encryptPassword(currentFormState.password)
                var finalImagePath = currentFormState.imageUri
                
                // Process image if it's a new URI (not already processed)
                if (currentFormState.imageUri != null && 
                    currentFormState.imageUri.startsWith("content://")) {
                    
                    val uri = Uri.parse(currentFormState.imageUri)
                    val userId = if (currentFormState.isEditMode) currentFormState.userId else null
                    
                    val processingResult = ImageUtils.saveImageToInternalStorage(
                        getApplication(),
                        uri,
                        userId
                    )
                    
                    if (processingResult.success) {
                        finalImagePath = processingResult.filePath
                        Log.d(TAG, "Image processed and saved: $finalImagePath")
                    } else {
                        _formState.value = currentFormState.copy(
                            isLoading = false,
                            errorMessage = processingResult.errorMessage ?: "Gagal memproses gambar"
                        )
                        return@launch
                    }
                }
                
                if (currentFormState.isEditMode && currentFormState.userId != null) {
                    // Update existing user
                    val existingUser = userRepository.getUserById(currentFormState.userId)
                    
                    // Delete old image if it's being replaced
                    if (existingUser?.image != null && 
                        existingUser.image != finalImagePath && 
                        finalImagePath != null) {
                        ImageUtils.deleteImage(existingUser.image)
                    }
                    
                    // Use existing password if new password is blank (no change)
                    val finalPassword = if (currentFormState.password.isBlank()) {
                        existingUser?.password ?: encryptedPassword
                    } else {
                        encryptedPassword
                    }
                    
                    val updatedUser = UserEntity(
                        id = currentFormState.userId,
                        name = currentFormState.name,
                        email = currentFormState.email,
                        password = finalPassword,
                        role = currentFormState.role,
                        isActive = currentFormState.isActive,
                        image = finalImagePath
                    )
                    
                    val result = userRepository.updateUser(updatedUser)
                    result.fold(
                        onSuccess = {
                            Log.d(TAG, "User updated successfully: ${currentFormState.userId}")
                            _formState.value = currentFormState.copy(
                                isLoading = false,
                                errorMessage = null,
                                imageUri = finalImagePath,
                                isOperationSuccessful = true
                            )
                            // Refresh user list
                            loadUsers()
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to update user", exception)
                            // Clean up the processed image if user update failed
                            if (finalImagePath != currentFormState.imageUri) {
                                ImageUtils.deleteImage(finalImagePath)
                            }
                            _formState.value = currentFormState.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Gagal memperbarui pengguna"
                            )
                        }
                    )
                } else {
                    // Create new user
                    val newUser = UserEntity(
                        name = currentFormState.name,
                        email = currentFormState.email,
                        password = encryptedPassword,
                        role = currentFormState.role,
                        isActive = currentFormState.isActive,
                        image = finalImagePath
                    )
                    
                    val result = userRepository.createUser(newUser)
                    result.fold(
                        onSuccess = { userId ->
                            Log.d(TAG, "User created successfully with ID: $userId")
                            _formState.value = currentFormState.copy(
                                isLoading = false,
                                errorMessage = null,
                                isOperationSuccessful = true
                            )
                            // Refresh user list
                            loadUsers()
                        },
                        onFailure = { exception ->
                            Log.e(TAG, "Failed to create user", exception)
                            // Clean up the processed image if user creation failed
                            if (finalImagePath != currentFormState.imageUri) {
                                ImageUtils.deleteImage(finalImagePath)
                            }
                            _formState.value = currentFormState.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Gagal membuat pengguna baru"
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during user save", e)
                _formState.value = currentFormState.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan yang tidak terduga"
                )
            }
        }
    }
    
    /**
     * Clears form error messages
     */
    fun clearFormError() {
        _formState.value = _formState.value.copy(errorMessage = null)
    }
    
    /**
     * Clears the operation success flag
     */
    fun clearOperationSuccess() {
        _formState.value = _formState.value.copy(isOperationSuccessful = false)
    }
    
    /**
     * Resets form after successful operation
     */
    fun resetFormAfterSuccess() {
        _formState.value = UserFormState()
    }
}