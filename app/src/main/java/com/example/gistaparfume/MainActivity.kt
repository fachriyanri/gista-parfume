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
                val windowSizeClass = calculateWindowSizeClass(this)
                val products by productViewModel.products.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()
                val isLoading = productViewModel.isLoading
                LaunchedEffect(Unit) {
                    productViewModel.initialize()
                }
                HomeScreen(
                    products = products,
                    isLoading = isLoading,
                    onLoadMore = { productViewModel.loadNextPage() },
                    categories = categories.map { it.title },
                    selectedCategory = categories.firstOrNull()?.title ?: "Semua Kategori",
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
                    onSortByPrice = { asc ->
                        // bisa implement sort di VM
                    },
                    widthSizeClass = windowSizeClass.widthSizeClass
                )
            }
        }
    }
}