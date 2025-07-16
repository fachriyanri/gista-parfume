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
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gistaparfume.data.viewmodel.CategoryViewModel
import com.example.gistaparfume.data.viewmodel.ProductViewModel
import com.example.gistaparfume.data.viewmodel.RegisterViewModel
import com.example.gistaparfume.ui.HomeScreen
import com.example.gistaparfume.ui.RegisterScreen
import com.example.gistaparfume.ui.theme.GistaParfumeTheme
import com.example.gistaparfume.utils.CustomToast

class MainActivity : ComponentActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GistaParfumeTheme {
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)
                
                val isAnyFilterActive by productViewModel.isAnyFilterActive.collectAsState()
                val selectedCategory by productViewModel.selectedCategory.collectAsState()
                val products by productViewModel.products.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()
                val isLoading = productViewModel.isLoading
                val sortDescending by productViewModel.sortDescending.collectAsState()

                LaunchedEffect(Unit) {
                    productViewModel.setupDatabase()
                    categoryViewModel.loadCategories()
                    productViewModel.initialize()
                }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
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
                            onResetFilters = { productViewModel.resetFilters() },
                            isAnyFilterActive = isAnyFilterActive,
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            onRegisterClick = {
                                navController.navigate("register")
                            }
                        )
                    }
                    
                    composable("register") {
                        RegisterScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = registerViewModel,
                            onNavigateToHome = {
                                // Navigate to home and clear the back stack to prevent back navigation
                                navController.navigate("home", NavOptions.Builder()
                                    .setPopUpTo("home", inclusive = false)
                                    .build())
                            },
                            onShowToast = { message ->
                                CustomToast.showRegistrationSuccessToast(this@MainActivity, message)
                            },
                            onNavigateToHomeFromHeader = {
                                // Navigate to home when Home button is clicked in header
                                navController.navigate("home")
                            }
                        )
                    }
                }
            }
        }
    }
}