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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(app: Application): AndroidViewModel(app) {
    private val repo = ProductRepository(
        AppDatabaseConfig.getDatabase(app).productDao()
    )

    // loading state
    var isLoading by mutableStateOf(false)
        private set

    // backing flow buat UI
//    private val _products = MutableStateFlow<List<Product>>(emptyList())
//    val products: StateFlow<List<Product>> = _products

    // REPLACE with this single line.
    val products: StateFlow<List<Product>> = repo.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Start collecting when the UI is visible
            initialValue = emptyList() // Start with an empty list
        )


//    init {
//        // load page pertama
//        loadNextPage()
//    }
//
//    /** Infinite scroll normal */
//    fun loadNextPage() {
//        if (isLoading) return
//        isLoading = true
//        viewModelScope.launch {
//            val newItems = repo.loadNextPage()                 // pake repo.loadNextPage()
//            Log.d("PRODUCT_VM", "Loaded ${newItems.size} new products from repository.")
//
//            _products.value = _products.value + newItems
//            isLoading = false
//        }
//    }

    /** Reset search state & clear produk lama */
//    fun resetSearch(keyword: String) {
//        repo.resetSearch(keyword)                              // reset internal repo
//        _products.value = emptyList()                          // clear UI list
//    }
//
//    /** Infinite scroll hasil search by keyword */
//    fun searchNextPage(keyword: String) {
//        if (isLoading) return
//        isLoading = true
//        viewModelScope.launch {
//            val newItems = repo.searchNextPage(keyword)        // ambil page selanjutnya berdasarkan keyword
//            _products.value = _products.value + newItems
//            isLoading = false
//        }
//    }
}