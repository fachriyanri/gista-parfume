package com.example.gistaparfume.data.repository

import android.util.Log
import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.error.CategoryError
import com.example.gistaparfume.data.error.toCategoryError
import com.example.gistaparfume.data.toUI
import com.example.gistaparfume.data.util.RetryUtil
import com.example.gistaparfume.data.validation.CategoryValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoryRepository(
    private val dao: CategoryDao
) {
    private val pageSize = 5

    // Pagination state management
    private var currentPage = 1
    private var endReached = false
    
    // Search pagination state management
    private var searchKeyword: String? = null
    private var searchPage = 1
    private var searchEndReached = false
    
    companion object {
        private const val TAG = "CategoryRepository"
    }

    fun getAllCategories(): Flow<List<CategoryEntity>> = dao.getAll()

    /**
     * Get categories with pagination support and error handling
     */
    suspend fun getCategoriesPage(page: Int, query: String = ""): List<CategoryEntity> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Getting categories page $page with query: '$query'")
        
        try {
            val offset = (page - 1) * pageSize
            
            val result = if (query.isBlank()) {
                // Get all categories with pagination
                dao.getPaged(pageSize, offset)
            } else {
                // Search categories with pagination
                val pattern = "%$query%"
                val allMatches = dao.search(pattern)
                allMatches.drop(offset).take(pageSize)
            }
            
            Log.d(TAG, "Retrieved ${result.size} categories for page $page")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error getting categories page", e)
            throw e.toCategoryError()
        }
    }

    /**
     * Get category by ID with error handling
     */
    suspend fun getCategoryById(id: Int): CategoryEntity? = withContext(Dispatchers.IO) {
        Log.d(TAG, "Getting category by ID: $id")
        
        try {
            val result = dao.getById(id)
            Log.d(TAG, "Category retrieved: ${result?.title ?: "not found"}")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error getting category by ID", e)
            throw e.toCategoryError()
        }
    }

    /**
     * Add new category with comprehensive validation and error handling
     */
    suspend fun addCategory(title: String, slug: String): Result<Long> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Adding category: title='$title', slug='$slug'")
        
        return@withContext RetryUtil.withDatabaseRetry {
            // Comprehensive validation using CategoryValidator
            val validationResult = CategoryValidator.validateCategory(title, slug)
            
            when (validationResult) {
                is CategoryValidator.ValidationResult.Error -> {
                    Log.w(TAG, "Validation failed: ${validationResult.error.message}")
                    throw validationResult.error
                }
                is CategoryValidator.ValidationResult.Success<*> -> {
                    val validatedData = validationResult.data as CategoryValidator.CategoryData
                    
                    // Check for duplicate title
                    val existingByTitle = dao.searchByTitle("%${validatedData.title}%")
                        .find { it.title.equals(validatedData.title, ignoreCase = true) }
                    if (existingByTitle != null) {
                        Log.w(TAG, "Duplicate title found: ${validatedData.title}")
                        throw CategoryError.DuplicateError("title", "Nama kategori '${validatedData.title}' sudah ada. Silakan gunakan nama yang berbeda.")
                    }
                    
                    // Check for duplicate slug
                    val existingBySlug = dao.getBySlug(validatedData.slug)
                    if (existingBySlug != null) {
                        Log.w(TAG, "Duplicate slug found: ${validatedData.slug}")
                        throw CategoryError.DuplicateError("slug", "Slug '${validatedData.slug}' sudah digunakan. Silakan gunakan slug yang berbeda.")
                    }

                    // Create new category entity
                    val category = CategoryEntity(
                        title = validatedData.title,
                        slug = validatedData.slug
                    )
                    
                    try {
                        val insertedId = dao.insert(category)
                        Log.d(TAG, "Category added successfully with ID: $insertedId")
                        insertedId
                    } catch (e: Exception) {
                        Log.e(TAG, "Database error while inserting category", e)
                        throw e.toCategoryError()
                    }
                }
            }
        }
    }

    /**
     * Update existing category with comprehensive validation and error handling
     */
    suspend fun updateCategory(id: Int, title: String, slug: String): Result<Int> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Updating category ID $id: title='$title', slug='$slug'")
        
        return@withContext RetryUtil.withDatabaseRetry {
            // Comprehensive validation using CategoryValidator
            val validationResult = CategoryValidator.validateCategory(title, slug)
            
            when (validationResult) {
                is CategoryValidator.ValidationResult.Error -> {
                    Log.w(TAG, "Validation failed for update: ${validationResult.error.message}")
                    throw validationResult.error
                }
                is CategoryValidator.ValidationResult.Success<*> -> {
                    val validatedData = validationResult.data as CategoryValidator.CategoryData
                    
                    // Check if category exists
                    val existingCategory = dao.getById(id)
                    if (existingCategory == null) {
                        Log.w(TAG, "Category not found for update: ID $id")
                        throw CategoryError.NotFoundError("Kategori tidak ditemukan.")
                    }
                    
                    // Check for duplicate title (excluding current category)
                    val existingByTitle = dao.searchByTitle("%${validatedData.title}%")
                        .find { it.title.equals(validatedData.title, ignoreCase = true) && it.id != id }
                    if (existingByTitle != null) {
                        Log.w(TAG, "Duplicate title found for update: ${validatedData.title}")
                        throw CategoryError.DuplicateError("title", "Nama kategori '${validatedData.title}' sudah ada. Silakan gunakan nama yang berbeda.")
                    }
                    
                    // Check for duplicate slug (excluding current category)
                    val existingBySlug = dao.getBySlug(validatedData.slug)
                    if (existingBySlug != null && existingBySlug.id != id) {
                        Log.w(TAG, "Duplicate slug found for update: ${validatedData.slug}")
                        throw CategoryError.DuplicateError("slug", "Slug '${validatedData.slug}' sudah digunakan. Silakan gunakan slug yang berbeda.")
                    }
                    
                    // Update category
                    val updatedCategory = existingCategory.copy(
                        title = validatedData.title,
                        slug = validatedData.slug
                    )
                    
                    try {
                        val updatedRows = dao.update(updatedCategory)
                        Log.d(TAG, "Category updated successfully. Rows affected: $updatedRows")
                        updatedRows
                    } catch (e: Exception) {
                        Log.e(TAG, "Database error while updating category", e)
                        throw e.toCategoryError()
                    }
                }
            }
        }
    }

    /**
     * Delete category by ID with enhanced error handling
     */
    suspend fun deleteCategory(id: Int): Result<Int> = withContext(Dispatchers.IO) {
        Log.d(TAG, "Deleting category ID: $id")
        
        return@withContext RetryUtil.withDatabaseRetry {
            try {
                val category = dao.getById(id)
                if (category == null) {
                    Log.w(TAG, "Category not found for deletion: ID $id")
                    throw CategoryError.NotFoundError("Kategori tidak ditemukan.")
                }
                
                val deletedRows = dao.delete(category)
                Log.d(TAG, "Category deleted successfully. Rows affected: $deletedRows")
                deletedRows
            } catch (e: Exception) {
                Log.e(TAG, "Database error while deleting category", e)
                throw e.toCategoryError()
            }
        }
    }

    /**
     * Reset pagination state
     */
    fun resetPagination() {
        currentPage = 1
        endReached = false
    }

    /**
     * Reset search pagination state
     */
    fun resetSearch() {
        searchKeyword = null
        searchPage = 1
        searchEndReached = false
    }

    /**
     * SEARCH kategori by title (LIKE '%keyword%') + paginate
     */
    suspend fun searchCategories(keyword: String, page: Int): List<Category> = withContext(Dispatchers.IO) {
        val pattern = "%$keyword%"
        // ambil semua hasil LIKE
        val allMatches: List<CategoryEntity> = dao.search(pattern)
        // terapkan pagination manual
        val offset = (page - 1) * pageSize
        allMatches
            .drop(offset)
            .take(pageSize)
            .map(CategoryEntity::toUI)
    }

    /**
     * COUNT total kategori,
     * kalo keyword null pake total semu, kalo ada keyword pake size search
     */
    suspend fun countCategories(keyword: String? = null): Int = withContext(Dispatchers.IO) {
        if (keyword.isNullOrBlank()) {
            dao.count()
        } else {
            dao.search("%$keyword%").size
        }
    }
}