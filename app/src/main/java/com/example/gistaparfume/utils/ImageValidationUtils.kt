package com.example.gistaparfume.utils

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for image validation in forms
 * Integrates with existing ValidationUtils pattern
 */
object ImageValidationUtils {
    
    /**
     * Data class representing image validation result
     * Follows the same pattern as other validation results
     */
    data class ImageValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    /**
     * Validates an image URI for use in user forms
     * 
     * @param context Application context
     * @param imageUri URI of the image to validate (can be null for optional images)
     * @param isRequired Whether the image is required or optional
     * @return ImageValidationResult containing validation status and error message if any
     */
    suspend fun validateImageForForm(
        context: Context, 
        imageUri: String?, 
        isRequired: Boolean = false
    ): ImageValidationResult {
        return withContext(Dispatchers.IO) {
            // Handle null/empty URI
            if (imageUri.isNullOrBlank()) {
                return@withContext if (isRequired) {
                    ImageValidationResult(false, "Foto profil wajib dipilih")
                } else {
                    ImageValidationResult(true) // Optional image, null is valid
                }
            }
            
            try {
                val uri = Uri.parse(imageUri)
                
                // If it's already a processed file path, check if it exists
                if (!imageUri.startsWith("content://")) {
                    return@withContext if (ImageUtils.imageExists(imageUri)) {
                        ImageValidationResult(true)
                    } else {
                        ImageValidationResult(false, "File gambar tidak ditemukan")
                    }
                }
                
                // Validate the content URI using ImageUtils
                val validationResult = ImageUtils.validateImage(context, uri)
                
                return@withContext ImageValidationResult(
                    validationResult.isValid,
                    validationResult.errorMessage
                )
                
            } catch (e: Exception) {
                return@withContext ImageValidationResult(
                    false,
                    "Format gambar tidak valid"
                )
            }
        }
    }
    
    /**
     * Quick validation for image URI without async operations
     * Used for immediate UI feedback
     * 
     * @param imageUri URI to validate
     * @param isRequired Whether the image is required
     * @return ImageValidationResult for immediate feedback
     */
    fun validateImageUriQuick(imageUri: String?, isRequired: Boolean = false): ImageValidationResult {
        if (imageUri.isNullOrBlank()) {
            return if (isRequired) {
                ImageValidationResult(false, "Foto profil wajib dipilih")
            } else {
                ImageValidationResult(true)
            }
        }
        
        // Basic URI format validation
        return try {
            Uri.parse(imageUri)
            ImageValidationResult(true)
        } catch (e: Exception) {
            ImageValidationResult(false, "Format gambar tidak valid")
        }
    }
    
    /**
     * Validates image file size from URI
     * 
     * @param context Application context
     * @param imageUri URI of the image
     * @return ImageValidationResult for size validation
     */
    suspend fun validateImageSize(context: Context, imageUri: String?): ImageValidationResult {
        return withContext(Dispatchers.IO) {
            if (imageUri.isNullOrBlank()) {
                return@withContext ImageValidationResult(true) // No image to validate
            }
            
            try {
                if (imageUri.startsWith("content://")) {
                    val uri = Uri.parse(imageUri)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val size = inputStream?.available() ?: 0
                    inputStream?.close()
                    
                    if (size > 5 * 1024 * 1024) { // 5MB limit
                        return@withContext ImageValidationResult(
                            false, 
                            "Ukuran file terlalu besar. Maksimal 5MB"
                        )
                    }
                } else {
                    // File path - check actual file size
                    val size = ImageUtils.getImageSize(imageUri)
                    if (size > 5 * 1024 * 1024) { // 5MB limit
                        return@withContext ImageValidationResult(
                            false, 
                            "Ukuran file terlalu besar. Maksimal 5MB"
                        )
                    }
                }
                
                return@withContext ImageValidationResult(true)
                
            } catch (e: Exception) {
                return@withContext ImageValidationResult(
                    false,
                    "Tidak dapat memvalidasi ukuran file"
                )
            }
        }
    }
}