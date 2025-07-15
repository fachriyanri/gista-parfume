package com.example.gistaparfume.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.Product
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductRepository(
        productDao = AppDatabaseConfig.getDatabase(app).productDao(),
        categoryDao = AppDatabaseConfig.getDatabase(app).categoryDao(),
        context = app.applicationContext
    )

    // --- STATE: The UI will observe these ---
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    var isLoading by mutableStateOf(false)
        private set

    // --- NEW: Filter criteria are now StateFlows for better observation ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("All") // 'All' is our default
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _sortDescending = MutableStateFlow<Boolean?>(null)
    val sortDescending: StateFlow<Boolean?> = _sortDescending

    // --- NEW: State to check if any filter is active ---
    val isAnyFilterActive: StateFlow<Boolean> = combine(
        _searchQuery,
        _selectedCategory,
        _sortDescending
    ) { query, category, sort ->
        // A filter is active if the query isn't blank, the category isn't "All", or a sort is applied.
        query.isNotBlank() || category != "All" || sort != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    // --- INTERNAL STATE: For managing pagination ---
    private var currentPage = 1
    private var isLastPage = false


    fun initialize() {
        viewModelScope.launch {
            repo.setupDatabaseIfNeeded()
            if (products.value.isEmpty()) {
                loadNextPage()
            }
        }
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) {
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val newProducts = repo.getProductsPage(
                    page = currentPage,
                    query = _searchQuery.value, // Use the value from StateFlow
                    category = _selectedCategory.value, // Use the value from StateFlow
                    isSortDesc = _sortDescending.value // Use the value from StateFlow
                )

                if (newProducts.isNotEmpty()) {
                    _products.value += newProducts
                    currentPage++
                } else {
                    isLastPage = true
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error loading products", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun onFilterChanged(query: String = this._searchQuery.value, category: String = this._selectedCategory.value) {
        _searchQuery.value = query
        _selectedCategory.value = category

        // Reset pagination and list, then load
        resetAndLoad()
    }

    fun onSortChanged(isDescending: Boolean) {
        // This logic toggles the sort: selecting the same one again turns it off.
        _sortDescending.value = if (_sortDescending.value == isDescending) {
            null
        } else {
            isDescending
        }
        resetAndLoad()
    }

    // --- NEW: The function for your Reset button ---
    fun resetFilters() {
        // Set all filter states back to their defaults
        _searchQuery.value = ""
        _selectedCategory.value = "All"
        _sortDescending.value = null

        // Reset pagination and list, then load
        resetAndLoad()
    }

    // --- NEW: Helper function to reduce repeated code ---
    private fun resetAndLoad() {
        currentPage = 1
        isLastPage = false
        _products.value = emptyList() // Clear the old list
        loadNextPage()
    }
}