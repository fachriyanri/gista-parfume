package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.RegisterFormState
import com.example.gistaparfume.data.repository.DatabaseConnectionException
import com.example.gistaparfume.data.repository.DatabaseException
import com.example.gistaparfume.data.repository.EmailAlreadyExistsException
import com.example.gistaparfume.data.repository.EncryptionException
import com.example.gistaparfume.data.repository.InvalidEmailFormatException
import com.example.gistaparfume.data.repository.InvalidInputException
import com.example.gistaparfume.data.repository.InvalidPasswordException
import com.example.gistaparfume.data.repository.UnexpectedErrorException
import com.example.gistaparfume.data.repository.UserRepository
import com.example.gistaparfume.utils.ValidationUtils
import kotlinx.coroutines.launch

class RegisterViewModel(app: Application) : AndroidViewModel(app) {
    private val userRepository = UserRepository.getInstance(app.applicationContext)
    
    // Form state management
    var formState by mutableStateOf(RegisterFormState())
        private set
    
    // Loading and error states
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    var isRegistrationSuccess by mutableStateOf(false)
        private set
    
    // Flag to prevent multiple success handling
    private var hasHandledSuccess by mutableStateOf(false)
        private set
    
    // Navigation callback for success redirect
    private var onNavigateToHome: (() -> Unit)? = null
    
    // Toast callback for success message
    private var onShowToast: ((String) -> Unit)? = null
    
    /**
     * Updates the name field in the form state
     */
    fun updateName(name: String) {
        formState = formState.copy(name = name)
    }
    
    /**
     * Updates the email field and validates format in real-time
     */
    fun updateEmail(email: String) {
        val validationResult = ValidationUtils.validateEmail(email)
        formState = formState.copy(
            email = email,
            isEmailValid = validationResult.isValid,
            emailError = validationResult.errorMessage
        )
    }
    
    /**
     * Updates the password field and validates length in real-time
     */
    fun updatePassword(password: String) {
        val validationResult = ValidationUtils.validatePassword(password)
        formState = formState.copy(
            password = password,
            isPasswordValid = validationResult.isValid,
            passwordError = validationResult.errorMessage
        )
        
        // Re-validate password confirmation when password changes
        if (formState.confirmPassword.isNotEmpty()) {
            updateConfirmPassword(formState.confirmPassword)
        }
    }
    
    /**
     * Updates the confirm password field and validates matching in real-time
     */
    fun updateConfirmPassword(confirmPassword: String) {
        val validationResult = ValidationUtils.validatePasswordConfirmation(
            formState.password, 
            confirmPassword
        )
        formState = formState.copy(
            confirmPassword = confirmPassword,
            isPasswordMatch = validationResult.isValid,
            confirmPasswordError = validationResult.errorMessage
        )
    }
    
    /**
     * Submits the registration form with comprehensive error handling
     */
    fun submitRegistration() {
        if (!formState.isFormValid) {
            errorMessage = "Mohon lengkapi semua field dengan benar"
            return
        }
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            hasHandledSuccess = false // Reset flag for new registration attempt
            
            try {
                val result = userRepository.registerUser(
                    name = formState.name.trim(),
                    email = formState.email.trim(),
                    password = formState.password
                )
                
                result.fold(
                    onSuccess = { userId ->
                        Log.d("RegisterViewModel", "User registered successfully with ID: $userId")
                        isRegistrationSuccess = true
                        clearForm()
                    },
                    onFailure = { exception ->
                        handleRegistrationError(exception)
                    }
                )
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Unexpected error during registration", e)
                errorMessage = "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * Handles different types of registration errors with comprehensive error messages
     */
    private fun handleRegistrationError(exception: Throwable) {
        errorMessage = when (exception) {
            is EmailAlreadyExistsException -> exception.message ?: "Email ini sudah terdaftar"
            is InvalidEmailFormatException -> exception.message ?: "Email tidak sesuai format"
            is InvalidPasswordException -> exception.message ?: "Input password sesuai dengan kriteria"
            is InvalidInputException -> exception.message ?: "Data yang dimasukkan tidak valid"
            is DatabaseConnectionException -> exception.message ?: "Tidak dapat terhubung ke database. Periksa koneksi Anda dan coba lagi."
            is DatabaseException -> exception.message ?: "Terjadi kesalahan saat menyimpan data. Silakan coba lagi."
            is EncryptionException -> "Terjadi kesalahan saat memproses data. Silakan coba lagi."
            is UnexpectedErrorException -> exception.message ?: "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
            else -> "Terjadi kesalahan yang tidak terduga. Silakan coba lagi."
        }
        Log.e("RegisterViewModel", "Registration error: ${exception.message}", exception)
    }
    
    /**
     * Sets the navigation callback for successful registration
     */
    fun setNavigationCallback(onNavigateToHome: () -> Unit) {
        this.onNavigateToHome = onNavigateToHome
    }
    
    /**
     * Sets the toast callback for success message
     */
    fun setToastCallback(onShowToast: (String) -> Unit) {
        this.onShowToast = onShowToast
    }
    
    /**
     * Handles successful registration with toast and navigation
     */
    fun handleRegistrationSuccess() {
        // Prevent multiple executions
        if (hasHandledSuccess) {
            return
        }
        
        hasHandledSuccess = true
        
        // Show success toast message
        onShowToast?.invoke("Registrasi berhasil")
        
        // Navigate to home and clear navigation stack
        onNavigateToHome?.invoke()
        
        // Reset success state
        resetRegistrationSuccess()
    }
    
    /**
     * Clears error messages
     */
    fun clearError() {
        errorMessage = null
    }
    
    /**
     * Resets registration success state
     */
    fun resetRegistrationSuccess() {
        isRegistrationSuccess = false
    }
    
    /**
     * Clears the entire form (used after successful registration)
     */
    private fun clearForm() {
        formState = RegisterFormState()
    }
    
    /**
     * Validates the entire form and returns validation status
     */
    fun validateForm(): Boolean {
        // Trigger validation for all fields
        updateEmail(formState.email)
        updatePassword(formState.password)
        updateConfirmPassword(formState.confirmPassword)
        
        return formState.isFormValid
    }
}