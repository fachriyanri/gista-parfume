package com.example.gistaparfume
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.gistaparfume.data.Product
import com.example.gistaparfume.ui.HomeScreen
import com.example.gistaparfume.ui.theme.GistaParfumeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GistaParfumeTheme {
                val windowSizeClass = calculateWindowSizeClass(this)

                val sampleProducts = listOf(
                    Product(1, "Bleau De Channel", 50000, "Parfum Pria", "Eau De Cologne", R.drawable.bleau_de_channel),
                    Product(2, "Dioresimo De Parfume", 100000, "Parfum Wanita", "Eau De Parfume", R.drawable.dioresimo_de_parfume)
                )
                val categories = listOf("Semua Kategori", "Extrait De Parfum", "Eau De Parfume", "Eau De Cologne")

                HomeScreen(
                    products = sampleProducts,
                    categories = categories,
                    selectedCategory = "Semua Kategori",
                    onAddToCart = { _, _ -> },
                    onCategorySelected = {},
                    onSearch = {},
                    onSortByPrice = {},
                    widthSizeClass = windowSizeClass.widthSizeClass
                )
            }
        }
    }
}