package com.example.gistaparfume
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gistaparfume.data.viewmodel.CategoryViewModel
import com.example.gistaparfume.data.viewmodel.ProductViewModel
import com.example.gistaparfume.ui.HomeScreen
import com.example.gistaparfume.ui.theme.GistaParfumeTheme

class MainActivity : ComponentActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GistaParfumeTheme {
                val isAnyFilterActive by productViewModel.isAnyFilterActive.collectAsState() // <-- Collect new state
                val selectedCategory by productViewModel.selectedCategory.collectAsState() // <-- Get selected category from VM

                val windowSizeClass = calculateWindowSizeClass(this)
                val products by productViewModel.products.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()
                val isLoading = productViewModel.isLoading
                val sortDescending by productViewModel.sortDescending.collectAsState() // <-- THE FIX

                LaunchedEffect(Unit) {
                    productViewModel.initialize()
                    categoryViewModel.loadCategories()
                }
                HomeScreen(
                    products = products,
                    isLoading = isLoading,
                    onLoadMore = { productViewModel.loadNextPage() },
                    categories = categories.map { it.title },
                    selectedCategory = selectedCategory,
                    onAddToCart = { product, qty ->
                        // implement kalau perlu
                    },
                    onCategorySelected = { category ->
                        // When a category is picked, call onFilterChanged
                        productViewModel.onFilterChanged(category = category)
                    },
                    onSearch = { query ->
                        // When user searches, call onFilterChanged
                        productViewModel.onFilterChanged(query = query)
                    },
                    onSortByPrice = { isDescending  ->
                        productViewModel.onSortChanged(isDescending)
                    },
                    sortDescending = sortDescending,
                    onResetFilters = { productViewModel.resetFilters() }, // <-- Pass the function
                    isAnyFilterActive = isAnyFilterActive,
                    widthSizeClass = windowSizeClass.widthSizeClass
                )
            }
        }
    }
}