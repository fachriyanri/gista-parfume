package com.example.gistaparfume.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.gistaparfume.R
import com.example.gistaparfume.data.Product


// Sample data untuk preview
private val sampleProducts = listOf(
    Product(
        id = 1,
        title = "Bleau De Channel",
        price = 50000,
        description = "Parfum Pria",
        category = "Eau De Cologne",
        imageRes = R.drawable.bleau_de_channel
    ),
    Product(
        id = 2,
        title = "Dioresimo De Parfume",
        price = 100000,
        description = "Parfum Wanita",
        category = "Eau De Parfume",
        imageRes = R.drawable.dioresimo_de_parfume
    )
)

private val sampleCategories = listOf(
    "Semua Kategori",
    "Extrait De Parfum",
    "Eau De Parfume",
    "Eau De Cologne"
)

@Preview(name = "Mobile Preview", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun HomeScreenMobilePreview() {
    HomeScreen(
        products = sampleProducts,
        categories = sampleCategories,
        selectedCategory = "Semua Kategori",
        onAddToCart = { _, _ -> },
        onCategorySelected = {},
        onSearch = {},
        onSortByPrice = {},
        widthSizeClass = WindowWidthSizeClass.Compact
    )
}

@Preview(name = "Tablet Preview", showBackground = true, widthDp = 1000, heightDp = 800)
@Composable
fun HomeScreenTabletPreview() {
    HomeScreen(
        products = sampleProducts,
        categories = sampleCategories,
        selectedCategory = "Semua Kategori",
        onAddToCart = { _, _ -> },
        onCategorySelected = {},
        onSearch = {},
        onSortByPrice = {},
        widthSizeClass = WindowWidthSizeClass.Expanded
    )
}

@Preview(name = "Medium Screen Preview", showBackground = true, widthDp = 700, heightDp = 800)
@Composable
fun HomeScreenMediumPreview() {
    HomeScreen(
        products = sampleProducts,
        categories = sampleCategories,
        selectedCategory = "Semua Kategori",
        onAddToCart = { _, _ -> },
        onCategorySelected = {},
        onSearch = {},
        onSortByPrice = {},
        widthSizeClass = WindowWidthSizeClass.Medium
    )
}
