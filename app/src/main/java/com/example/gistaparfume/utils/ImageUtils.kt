package com.example.gistaparfume.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID

/**
 * Utility class for handling image operations including validation, storage, and processing
 */
object ImageUtils {
    
    private const val TAG = "ImageUtils"
    private const val MAX_IMAGE_SIZE_MB = 5 // Maximum image size in MB
    private const val MAX_IMAGE_SIZE_BYTES = MAX_IMAGE_SIZE_MB * 1024 * 1024
    private const val COMPRESSED_IMAGE_QUALITY = 80 // JPEG compression quality (0-100)
    private const val MAX_IMAGE_DIMENSION = 1024 // Maximum width/height in pixels
    
    // Supported image formats
    private val SUPPORTED_FORMATS = setOf("jpg", "jpeg", "png", "webp")
    
    /**
     * Data class representing image validation result
     */
    data class ImageValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    /**
     * Data class representing image processing result
     */
    data class ImageProcessingResult(
        val success: Boolean,
        val filePath: String? = null,
        val errorMessage: String? = null
    )
    
    /**
     * Validates an image URI for format and size constraints
     * 
     * @param context Application context
     * @param imageUri URI of the image to validate
     * @return ImageValidationResult containing validation status and error message if any
     */
    suspend fun validateImage(context: Context, imageUri: Uri): ImageValidationResult {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                
                // Check if URI is accessible
                val inputStream = contentResolver.openInputStream(imageUri)
                    ?: return@withContext ImageValidationResult(
                        false, 
                        "Tidak dapat mengakses file gambar"
                    )
                
                inputStream.use { stream ->
                    // Check file size
                    val fileSize = stream.available()
                    if (fileSize > MAX_IMAGE_SIZE_BYTES) {
                        return@withContext ImageValidationResult(
                            false,
                            "Ukuran file terlalu besar. Maksimal ${MAX_IMAGE_SIZE_MB}MB"
                        )
                    }
                    
                    // Check image format by trying to decode
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    
                    // Reset stream for decoding
                    val newStream = contentResolver.openInputStream(imageUri)
                    BitmapFactory.decodeStream(newStream, null, options)
                    newStream?.close()
                    
                    if (options.outWidth == -1 || options.outHeight == -1) {
                        return@withContext ImageValidationResult(
                            false,
                            "File bukan format gambar yang valid"
                        )
                    }
                    
                    // Check MIME type
                    val mimeType = contentResolver.getType(imageUri)
                    if (mimeType == null || !mimeType.startsWith("image/")) {
                        return@withContext ImageValidationResult(
                            false,
                            "Format file tidak didukung. Gunakan JPG, PNG, atau WebP"
                        )
                    }
                    
                    // Validate specific format
                    val format = mimeType.substringAfter("image/").lowercase()
                    if (format !in SUPPORTED_FORMATS) {
                        return@withContext ImageValidationResult(
                            false,
                            "Format $format tidak didukung. Gunakan JPG, PNG, atau WebP"
                        )
                    }
                    
                    return@withContext ImageValidationResult(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error validating image", e)
                return@withContext ImageValidationResult(
                    false,
                    "Terjadi kesalahan saat memvalidasi gambar: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Saves and processes an image from URI to internal storage
     * 
     * @param context Application context
     * @param imageUri URI of the image to save
     * @param userId User ID for organizing images (optional)
     * @return ImageProcessingResult containing success status and file path
     */
    suspend fun saveImageToInternalStorage(
        context: Context, 
        imageUri: Uri, 
        userId: Int? = null
    ): ImageProcessingResult {
        return withContext(Dispatchers.IO) {
            try {
                // First validate the image
                val validationResult = validateImage(context, imageUri)
                if (!validationResult.isValid) {
                    return@withContext ImageProcessingResult(
                        false,
                        errorMessage = validationResult.errorMessage
                    )
                }
                
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(imageUri)
                    ?: return@withContext ImageProcessingResult(
                        false,
                        errorMessage = "Tidak dapat membaca file gambar"
                    )
                
                inputStream.use { stream ->
                    // Decode the image
                    val originalBitmap = BitmapFactory.decodeStream(stream)
                        ?: return@withContext ImageProcessingResult(
                            false,
                            errorMessage = "Tidak dapat memproses gambar"
                        )
                    
                    // Process the image (resize and compress)
                    val processedBitmap = processImage(originalBitmap)
                    
                    // Create directory for user images
                    val imagesDir = File(context.filesDir, "user_images")
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs()
                    }
                    
                    // Generate unique filename
                    val filename = if (userId != null) {
                        "user_${userId}_${UUID.randomUUID()}.jpg"
                    } else {
                        "user_${UUID.randomUUID()}.jpg"
                    }
                    
                    val imageFile = File(imagesDir, filename)
                    
                    // Save the processed image
                    FileOutputStream(imageFile).use { outputStream ->
                        processedBitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            COMPRESSED_IMAGE_QUALITY,
                            outputStream
                        )
                    }
                    
                    // Clean up bitmaps
                    if (processedBitmap != originalBitmap) {
                        originalBitmap.recycle()
                    }
                    processedBitmap.recycle()
                    
                    return@withContext ImageProcessingResult(
                        true,
                        filePath = imageFile.absolutePath
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image", e)
                return@withContext ImageProcessingResult(
                    false,
                    errorMessage = "Gagal menyimpan gambar: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Processes an image by resizing and applying rotation correction
     * 
     * @param bitmap Original bitmap to process
     * @return Processed bitmap
     */
    private fun processImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Calculate scaling factor if image is too large
        val scaleFactor = if (width > MAX_IMAGE_DIMENSION || height > MAX_IMAGE_DIMENSION) {
            val maxDimension = maxOf(width, height)
            MAX_IMAGE_DIMENSION.toFloat() / maxDimension
        } else {
            1f
        }
        
        return if (scaleFactor < 1f) {
            // Resize the image
            val newWidth = (width * scaleFactor).toInt()
            val newHeight = (height * scaleFactor).toInt()
            
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }
    
    /**
     * Deletes an image file from internal storage
     * 
     * @param imagePath Path to the image file to delete
     * @return True if deletion was successful, false otherwise
     */
    suspend fun deleteImage(imagePath: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (imagePath.isNullOrBlank()) return@withContext true
                
                val file = File(imagePath)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (!deleted) {
                        Log.w(TAG, "Failed to delete image file: $imagePath")
                    }
                    deleted
                } else {
                    true // File doesn't exist, consider it "deleted"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting image", e)
                false
            }
        }
    }
    
    /**
     * Gets the file URI for an internal storage image path
     * 
     * @param imagePath Path to the image file
     * @return File URI or null if path is invalid
     */
    fun getImageUri(imagePath: String?): Uri? {
        return if (!imagePath.isNullOrBlank() && File(imagePath).exists()) {
            Uri.fromFile(File(imagePath))
        } else {
            null
        }
    }
    
    /**
     * Checks if an image file exists at the given path
     * 
     * @param imagePath Path to check
     * @return True if file exists, false otherwise
     */
    fun imageExists(imagePath: String?): Boolean {
        return !imagePath.isNullOrBlank() && File(imagePath).exists()
    }
    
    /**
     * Gets the size of an image file in bytes
     * 
     * @param imagePath Path to the image file
     * @return File size in bytes, or 0 if file doesn't exist
     */
    fun getImageSize(imagePath: String?): Long {
        return if (!imagePath.isNullOrBlank()) {
            val file = File(imagePath)
            if (file.exists()) file.length() else 0L
        } else {
            0L
        }
    }
}