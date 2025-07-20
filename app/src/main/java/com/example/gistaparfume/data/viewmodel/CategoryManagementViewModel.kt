package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.error.CategoryError
import com.example.gistaparfume.data.error.toCategoryError
import com.example.gistaparfume.data.repository.CategoryRepository
import com.example.gistaparfume.data.toUI
import com.example.gistaparfume.data.util.RetryUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryManagementViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CategoryRepository(
        AppDatabaseConfig.getDatabase(app).categoryDao()
    )

    // UI State data class for screen state with pagination support
    data class CategoryManagementUiState(
        val categories: List<Category> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val searchQuery: String = "",
        val isSearchActive: Boolean = false,
        val isDeleting: Boolean = false
    )

    // State management
    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState

    // StateFlow for searchQuery and filtering state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Pagination state variables
    private var currentPage = 1
    private var isLastPage = false

    /**
     * Initialize method to load first page of categories
     */
    fun initialize() {
        viewModelScope.launch {
            if (_uiState.value.categories.isEmpty()) {
                loadNextPage()
            }
        }
    }

    /**
     * Load next page method to fetch categories in chunks of 5 with retry mechanism
     */
    fun loadNextPage() {
        if (_uiState.value.isLoading || isLastPage) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Use retry mechanism for loading categories
            val result = RetryUtil.withNetworkRetry {
                repo.getCategoriesPage(
                    page = currentPage,
                    query = _searchQuery.value
                )
            }
            
            result.fold(
                onSuccess = { newCategories ->
                    if (newCategories.isNotEmpty()) {
                        val uiCategories = newCategories.map { it.toUI() }

                        _uiState.update { currentState ->
                            currentState.copy(
                                categories = currentState.categories + uiCategories,
                                isLoading = false,
                                isSearchActive = _searchQuery.value.isNotBlank()
                            )
                        }
                        currentPage++
                    } else {
                        isLastPage = true
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                },
                onFailure = { exception ->
                    Log.e("CategoryManagementViewModel", "Error loading categories", exception)
                    
                    // Enhanced error handling with specific CategoryError types
                    val errorMessage = when (val categoryError = exception.toCategoryError()) {
                        is CategoryError.NetworkError -> categoryError.message
                        is CategoryError.DatabaseError -> categoryError.message
                        is CategoryError.TimeoutError -> categoryError.message
                        is CategoryError.UnknownError -> categoryError.message
                        else -> "Gagal memuat kategori. Silakan coba lagi."
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }
    
    /**
     * Retry loading categories after an error
     */
    fun retryLoadCategories() {
        Log.d("CategoryManagementViewModel", "Retrying to load categories")
        _uiState.value = _uiState.value.copy(errorMessage = null)
        loadNextPage()
    }

    /**
     * Filter changed method for search functionality with pagination reset
     */
    fun onFilterChanged(query: String) {
        _searchQuery.value = query
        
        // Update UI state with new search query
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isSearchActive = query.isNotBlank()
        )
        
        // Reset pagination and reload
        resetAndLoad()
    }

    /**
     * Reset filters method to clear search and reload from first page
     */
    fun resetFilters() {
        _searchQuery.value = ""
        
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            isSearchActive = false,
            errorMessage = null
        )
        
        // Reset pagination and reload
        resetAndLoad()
    }

    /**
     * Delete category method with enhanced error handling and retry mechanism
     */
    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)
            
            try {
                val result = repo.deleteCategory(categoryId)
                
                result.fold(
                    onSuccess = { deletedRows ->
                        Log.d("CategoryManagementViewModel", "Category deleted successfully. Rows affected: $deletedRows")
                        
                        // Remove the deleted category from the current list
                        val updatedCategories = _uiState.value.categories.filter { it.id != categoryId }
                        _uiState.value = _uiState.value.copy(
                            categories = updatedCategories,
                            isDeleting = false
                        )
                        
                        // If the list becomes empty or we're near the end, try to load more
                        if (updatedCategories.isEmpty() || updatedCategories.size < 5) {
                            loadNextPage()
                        }
                    },
                    onFailure = { exception ->
                        Log.e("CategoryManagementViewModel", "Failed to delete category", exception)
                        
                        // Enhanced error handling with specific CategoryError types
                        val errorMessage = when (val categoryError = exception.toCategoryError()) {
                            is CategoryError.NotFoundError -> categoryError.message
                            is CategoryError.DatabaseError -> categoryError.message
                            is CategoryError.NetworkError -> categoryError.message
                            is CategoryError.TimeoutError -> categoryError.message
                            is CategoryError.PermissionError -> categoryError.message
                            is CategoryError.UnknownError -> categoryError.message
                            else -> "Gagal menghapus kategori. Silakan coba lagi."
                        }
                        
                        _uiState.value = _uiState.value.copy(
                            isDeleting = false,
                            errorMessage = errorMessage
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("CategoryManagementViewModel", "Unexpected error deleting category", e)
                val categoryError = e.toCategoryError()
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = categoryError.message
                )
            }
        }
    }

    /**
     * Helper method to reset pagination and reload data
     */
    internal fun resetAndLoad() {
        currentPage = 1
        isLastPage = false
        _uiState.value = _uiState.value.copy(categories = emptyList())
        loadNextPage()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}