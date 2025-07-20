package com.example.gistaparfume.data.validation

import com.example.gistaparfume.data.error.CategoryError
import java.util.regex.Pattern

/**
 * Comprehensive validation utility for category data
 */
object CategoryValidator {
    
    // Validation constants
    private const val MIN_TITLE_LENGTH = 2
    private const val MAX_TITLE_LENGTH = 100
    private const val MIN_SLUG_LENGTH = 2
    private const val MAX_SLUG_LENGTH = 100
    
    // Regex patterns
    private val SLUG_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$")
    private val TITLE_PATTERN = Pattern.compile("^[\\p{L}\\p{N}\\s._-]+$") // Letters, numbers, spaces, dots, underscores, hyphens
    
    // Reserved slugs that shouldn't be used
    private val RESERVED_SLUGS = setOf(
        "admin", "api", "www", "mail", "ftp", "localhost", "root", "test", 
        "staging", "dev", "development", "prod", "production", "null", "undefined"
    )
    
    /**
     * Validates category title with comprehensive rules
     * @param title The title to validate
     * @return ValidationResult with success/error information
     */
    fun validateTitle(title: String?): ValidationResult {
        return when {
            title.isNullOrBlank() -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori tidak boleh kosong")
            )
            title.trim().length < MIN_TITLE_LENGTH -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori minimal $MIN_TITLE_LENGTH karakter")
            )
            title.trim().length > MAX_TITLE_LENGTH -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori maksimal $MAX_TITLE_LENGTH karakter")
            )
            !TITLE_PATTERN.matcher(title.trim()).matches() -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori hanya boleh mengandung huruf, angka, spasi, titik, underscore, dan tanda hubung")
            )
            title.trim().startsWith(" ") || title.trim().endsWith(" ") -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori tidak boleh dimulai atau diakhiri dengan spasi")
            )
            containsConsecutiveSpaces(title.trim()) -> ValidationResult.Error(
                CategoryError.ValidationError("title", "Nama kategori tidak boleh mengandung spasi berturut-turut")
            )
            else -> ValidationResult.Success(title.trim())
        }
    }
    
    /**
     * Validates category slug with comprehensive rules
     * @param slug The slug to validate
     * @return ValidationResult with success/error information
     */
    fun validateSlug(slug: String?): ValidationResult {
        return when {
            slug.isNullOrBlank() -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug tidak boleh kosong")
            )
            slug.trim().length < MIN_SLUG_LENGTH -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug minimal $MIN_SLUG_LENGTH karakter")
            )
            slug.trim().length > MAX_SLUG_LENGTH -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug maksimal $MAX_SLUG_LENGTH karakter")
            )
            !SLUG_PATTERN.matcher(slug.trim()).matches() -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug hanya boleh mengandung huruf, angka, underscore, dan tanda hubung")
            )
            slug.trim().startsWith("-") || slug.trim().endsWith("-") -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug tidak boleh dimulai atau diakhiri dengan tanda hubung")
            )
            slug.trim().startsWith("_") || slug.trim().endsWith("_") -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug tidak boleh dimulai atau diakhiri dengan underscore")
            )
            containsConsecutiveHyphens(slug.trim()) -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug tidak boleh mengandung tanda hubung berturut-turut")
            )
            containsConsecutiveUnderscores(slug.trim()) -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug tidak boleh mengandung underscore berturut-turut")
            )
            RESERVED_SLUGS.contains(slug.trim().lowercase()) -> ValidationResult.Error(
                CategoryError.ValidationError("slug", "Slug '${slug.trim()}' adalah kata yang tidak diizinkan")
            )
            else -> ValidationResult.Success(slug.trim().lowercase())
        }
    }
    
    /**
     * Validates both title and slug together
     * @param title The title to validate
     * @param slug The slug to validate
     * @return ValidationResult with success/error information
     */
    fun validateCategory(title: String?, slug: String?): ValidationResult {
        val titleResult = validateTitle(title)
        val slugResult = validateSlug(slug)
        
        return when {
            titleResult is ValidationResult.Error -> titleResult
            slugResult is ValidationResult.Error -> slugResult
            else -> ValidationResult.Success(
                CategoryData(
                    title = (titleResult as ValidationResult.Success<String>).data,
                    slug = (slugResult as ValidationResult.Success<String>).data
                )
            )
        }
    }
    
    /**
     * Generates a slug from a title
     * @param title The title to convert to slug
     * @return Generated slug
     */
    fun generateSlugFromTitle(title: String): String {
        return title.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "") // Remove special characters except spaces and hyphens
            .replace(Regex("\\s+"), "-") // Replace spaces with hyphens
            .replace(Regex("-+"), "-") // Replace multiple hyphens with single hyphen
            .trim('-') // Remove leading/trailing hyphens
            .take(MAX_SLUG_LENGTH) // Limit length
    }
    
    /**
     * Checks if string contains consecutive spaces
     */
    private fun containsConsecutiveSpaces(text: String): Boolean {
        return text.contains("  ")
    }
    
    /**
     * Checks if string contains consecutive hyphens
     */
    private fun containsConsecutiveHyphens(text: String): Boolean {
        return text.contains("--")
    }
    
    /**
     * Checks if string contains consecutive underscores
     */
    private fun containsConsecutiveUnderscores(text: String): Boolean {
        return text.contains("__")
    }
    
    /**
     * Data class for validated category data
     */
    data class CategoryData(
        val title: String,
        val slug: String
    )
    
    /**
     * Sealed class representing validation results
     */
    sealed class ValidationResult {
        data class Success<T>(val data: T) : ValidationResult()
        data class Error(val error: CategoryError) : ValidationResult()
    }
}