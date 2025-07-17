package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.AuthResult
import com.example.gistaparfume.data.LoginUiState
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.repository.AuthRepository
import com.example.gistaparfume.data.repository.AuthRepositoryImpl
import com.example.gistaparfume.data.repository.EmailService
import com.example.gistaparfume.data.repository.EmailServiceImpl
import com.example.gistaparfume.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    
    // Dependencies
    private val emailService: EmailService = EmailServiceImpl.getInstance()
    private val authRepository: AuthRepository = AuthRepositoryImpl.getInstance(app.applicationContext, emailService)
    
    // UI State management using StateFlow
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    init {
        // Restore authentication state on ViewModel initialization
        restoreAuthenticationState()
    }
    
    // Form field states
    var email by mutableStateOf("")
        private set
    
    var password by mutableStateOf("")
        private set
    
    var resetEmail by mutableStateOf("")
        private set
    
    // Validation states
    var isEmailValid by mutableStateOf(true)
        private set
    
    var emailError by mutableStateOf<String?>(null)
        private set
    
    /**
     * Updates the email field and validates format in real-time
     */
    fun updateEmail(newEmail: String) {
        email = newEmail
        val validationResult = ValidationUtils.validateEmail(newEmail)
        isEmailValid = validationResult.isValid
        emailError = if (newEmail.isNotEmpty() && !validationResult.isValid) {
            validationResult.errorMessage
        } else null
    }
    
    /**
     * Updates the password field
     */
    fun updatePassword(newPassword: String) {
        password = newPassword
    }
    
    /**
     * Updates the reset email field for password reset
     */
    fun updateResetEmail(newResetEmail: String) {
        resetEmail = newResetEmail
    }
    
    /**
     * Performs user login with email and password validation
     */
    fun login() {
        // Validate inputs
        if (email.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email tidak boleh kosong"
            )
            return
        }
        
        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password tidak boleh kosong"
            )
            return
        }
        
        // Validate email format
        val emailValidation = ValidationUtils.validateEmail(email.trim())
        if (!emailValidation.isValid) {
            _uiState.value = _uiState.value.copy(
                errorMessage = emailValidation.errorMessage ?: "Format email tidak valid"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            
            try {
                val result = authRepository.authenticateUser(email.trim(), password)
                
                when (result) {
                    is AuthResult.Success -> {
                        Log.d("LoginViewModel", "Login successful for user: ${result.user.email}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentUser = result.user,
                            errorMessage = null
                        )
                        clearForm()
                    }
                    
                    is AuthResult.EmailNotFound -> {
                        Log.w("LoginViewModel", "Login failed: Email not found")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Email tidak terdaftar silahkan lakukan registrasi terlebih dahulu"
                        )
                    }
                    
                    is AuthResult.InvalidPassword -> {
                        Log.w("LoginViewModel", "Login failed: Invalid password")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Password yang Anda masukkan salah"
                        )
                    }
                    
                    is AuthResult.Error -> {
                        Log.e("LoginViewModel", "Login failed: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error during login", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Sends password reset email to the specified email address
     */
    fun resetPassword(emailAddress: String = resetEmail) {
        // Validate email input
        if (emailAddress.trim().isEmpty()) {
            _uiState.value = _uiState.value.copy(
                passwordResetErrorMessage = "Email tidak boleh kosong"
            )
            return
        }
        
        // Validate email format
        val emailValidation = ValidationUtils.validateEmail(emailAddress.trim())
        if (!emailValidation.isValid) {
            _uiState.value = _uiState.value.copy(
                passwordResetErrorMessage = emailValidation.errorMessage ?: "Format email tidak valid"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isPasswordResetLoading = true,
                passwordResetErrorMessage = null,
                isPasswordResetSent = false
            )
            
            try {
                val result = authRepository.sendPasswordResetEmail(emailAddress.trim())
                
                result.fold(
                    onSuccess = {
                        Log.d("LoginViewModel", "Password reset email sent successfully to: $emailAddress")
                        _uiState.value = _uiState.value.copy(
                            isPasswordResetLoading = false,
                            isPasswordResetSent = true,
                            passwordResetErrorMessage = null
                        )
                        resetEmail = "" // Clear the reset email field
                    },
                    onFailure = { exception ->
                        Log.e("LoginViewModel", "Failed to send password reset email", exception)
                        _uiState.value = _uiState.value.copy(
                            isPasswordResetLoading = false,
                            passwordResetErrorMessage = exception.message ?: "Gagal mengirim email reset password"
                        )
                    }
                )
                
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error during password reset", e)
                _uiState.value = _uiState.value.copy(
                    isPasswordResetLoading = false,
                    passwordResetErrorMessage = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
                )
            }
        }
    }
    
    /**
     * Logs out the current user
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _uiState.value = LoginUiState() // Reset to initial state
                clearForm()
                Log.d("LoginViewModel", "User logged out successfully")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during logout", e)
            }
        }
    }
    
    /**
     * Gets the current authenticated user
     */
    fun getCurrentUser(): UserEntity? {
        return _uiState.value.currentUser
    }
    
    /**
     * Checks if user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return _uiState.value.isLoggedIn
    }
    
    /**
     * Clears error messages
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Clears password reset error messages
     */
    fun clearPasswordResetError() {
        _uiState.value = _uiState.value.copy(passwordResetErrorMessage = null)
    }
    
    /**
     * Resets password reset sent state
     */
    fun resetPasswordResetSentState() {
        _uiState.value = _uiState.value.copy(isPasswordResetSent = false)
    }
    
    /**
     * Clears the login form fields
     */
    private fun clearForm() {
        email = ""
        password = ""
        isEmailValid = true
        emailError = null
    }
    
    /**
     * Validates the login form
     */
    fun validateLoginForm(): Boolean {
        val emailValidation = ValidationUtils.validateEmail(email.trim())
        val isFormValid = email.trim().isNotEmpty() && 
                         password.isNotEmpty() && 
                         emailValidation.isValid
        
        if (!isFormValid) {
            if (email.trim().isEmpty()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Email tidak boleh kosong")
            } else if (password.isEmpty()) {
                _uiState.value = _uiState.value.copy(errorMessage = "Password tidak boleh kosong")
            } else if (!emailValidation.isValid) {
                _uiState.value = _uiState.value.copy(errorMessage = emailValidation.errorMessage ?: "Format email tidak valid")
            }
        }
        
        return isFormValid
    }
    
    /**
     * Restores authentication state from persistent storage on app startup
     */
    private fun restoreAuthenticationState() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = true,
                        currentUser = currentUser
                    )
                    Log.d("LoginViewModel", "Authentication state restored for user: ${currentUser.email}")
                } else {
                    Log.d("LoginViewModel", "No stored authentication state found")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error restoring authentication state", e)
                // If there's an error, ensure we're in a clean state
                _uiState.value = LoginUiState()
            }
        }
    }
    
    /**
     * Manually checks and restores authentication state
     * Can be called from MainActivity or other components
     */
    fun checkAuthenticationState() {
        restoreAuthenticationState()
    }
    
    /**
     * Resets password using a reset token
     */
    fun resetPasswordWithToken(
        email: String,
        resetToken: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = authRepository.resetPasswordWithToken(email, resetToken, newPassword)
                
                result.fold(
                    onSuccess = {
                        Log.d("LoginViewModel", "Password reset successful")
                        onSuccess()
                    },
                    onFailure = { exception ->
                        Log.e("LoginViewModel", "Password reset failed", exception)
                        onError(exception.message ?: "Gagal mereset password")
                    }
                )
                
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Unexpected error during password reset", e)
                onError("Terjadi kesalahan yang tidak terduga. Silakan coba lagi.")
            }
        }
    }
}