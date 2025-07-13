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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ProductRepository(
        productDao = AppDatabaseConfig.getDatabase(app).productDao(),
        categoryDao = AppDatabaseConfig.getDatabase(app).categoryDao(),
        context = app.applicationContext // <-- Pass the context here
    )

    // STATE: The UI will observe these
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    var isLoading by mutableStateOf(false)
        private set

    // INTERNAL STATE: For managing queries and pagination
    private var currentPage = 1
    private var searchQuery = ""
    private var selectedCategory = "All" // 'All' is our default
    private var isLastPage = false

    fun initialize() {
        viewModelScope.launch {
            // Step 1: Ensure the database is set up. This will wait until it's done.
            repo.setupDatabaseIfNeeded()

            // Step 2: Now that we know the data is ready, load the first page.
            if (products.value.isEmpty()) {
                loadNextPage()
            }
        }
    }

    fun loadNextPage() {
        val logTag = "PRODUCT_VM_TRACE"

        Log.d(logTag, "loadNextPage function called.")

        // Prevent multiple simultaneous loads or loading beyond the last page
        if (isLoading || isLastPage) {
            Log.d(logTag, "Execution stopped: isLoading=$isLoading, isLastPage=$isLastPage")
            return
        }

        viewModelScope.launch {
            Log.d(logTag, "Coroutine launched. Setting isLoading to true.")
            isLoading = true
            try {
                Log.d(logTag, "Calling repository to get page: $currentPage")
                val newProducts = repo.getProductsPage(
                    page = currentPage,
                    query = searchQuery,
                    category = selectedCategory
                )
                Log.d(logTag, "Repository returned ${newProducts.size} new products.")

                if (newProducts.isNotEmpty()) {
                    _products.value = _products.value + newProducts
                    currentPage++
                    Log.d(logTag, "Products appended. New total count: ${_products.value.size}")
                } else {
                    isLastPage = true
                    Log.d(logTag, "No new products found. Marking as last page.")
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e(logTag, "Error loading products", e)
            } finally {
                isLoading = false
                Log.d(logTag, "Coroutine finished. Set isLoading to false.")
            }
        }
    }

    fun onFilterChanged(query: String = this.searchQuery, category: String = this.selectedCategory) {
        // Reset everything when a filter changes
        this.searchQuery = query
        this.selectedCategory = category
        this.currentPage = 1
        this.isLastPage = false
        _products.value = emptyList() // Clear the old list

        // Load the first page with the new filters
        loadNextPage()
    }
}