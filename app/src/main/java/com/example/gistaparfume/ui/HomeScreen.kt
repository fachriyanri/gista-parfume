package com.example.gistaparfume.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.gistaparfume.data.Product
import com.example.gistaparfume.ui.components.ModalSidebarContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    products: List<Product>,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    categories: List<String>,
    selectedCategory: String?,
    onAddToCart: (Product, Int) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSortByPrice: (Boolean) -> Unit,
    sortDescending: Boolean?,
    widthSizeClass: WindowWidthSizeClass
) {
    var showSidebarMobile by remember { mutableStateOf(false) }

    when (widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            // ===== TABLET / DESKTOP =====
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 8.dp)
                ) {
                    TopHeader(widthSizeClass)
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    MainHeader()
                    FilterBar(
                        selectedCategory = selectedCategory,
                        onSortByPrice = onSortByPrice,
                        onFilterClick = { showSidebarMobile = !showSidebarMobile },
                        widthSizeClass = widthSizeClass,
                        sortDescending = sortDescending
                    )
                    Spacer(modifier = Modifier.height(46.dp))

                    // grid 2 kolom, fill sisa height
                    MainContent(
                        products = products,
                        isLoading = isLoading,
                        onLoadMore = onLoadMore,
                        onAddToCart = onAddToCart,
                        widthSizeClass = widthSizeClass,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 16.dp)
                    )

                    Footer(widthSizeClass)
                }

                Sidebar(
                    categories = categories,
                    onCategorySelected = onCategorySelected,
                    onSearch = onSearch,
                    modifier = Modifier
                        .width(220.dp)
                        .fillMaxHeight()
                )
            }
        }
        WindowWidthSizeClass.Medium -> {
            // ===== MEDIUM PHONE =====
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopHeader(widthSizeClass)
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    MainHeader()
                    FilterBar(
                        selectedCategory = selectedCategory,
                        onSortByPrice = onSortByPrice,
                        onFilterClick = { showSidebarMobile = !showSidebarMobile },
                        widthSizeClass = widthSizeClass,
                        sortDescending = sortDescending
                    )
                    Spacer(modifier = Modifier.height(76.dp))

                    // grid 2 kolom, wrap height
                    MainContent(
                        products = products,
                        isLoading = isLoading,
                        onLoadMore = onLoadMore,
                        onAddToCart = onAddToCart,
                        widthSizeClass = widthSizeClass,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(86.dp))
                    Footer(widthSizeClass)
                }
                if (showSidebarMobile) {
                    ModalBottomSheet(onDismissRequest = { showSidebarMobile = false }) {
                        ModalSidebarContent(
                            categories = categories,
                            onCategorySelected = {
                                onCategorySelected(it)
                                showSidebarMobile = false
                            },
                            onSearch = {
                                onSearch(it)
                                showSidebarMobile = false
                            }
                        )
                    }
                }
            }
        }
        else -> {
            // ===== COMPACT / MOBILE =====
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopHeader(widthSizeClass)
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    MainHeader()
                    FilterBar(
                        selectedCategory = selectedCategory,
                        onSortByPrice = onSortByPrice,
                        onFilterClick = { showSidebarMobile = !showSidebarMobile },
                        widthSizeClass = widthSizeClass,
                        sortDescending = sortDescending
                    )

                    // grid 1 kolom, fill sisa height
                    MainContent(
                        products = products,
                        isLoading = isLoading,
                        onLoadMore = onLoadMore,
                        onAddToCart = onAddToCart,
                        widthSizeClass = widthSizeClass,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(86.dp))

                    Footer(widthSizeClass)
                }
                if (showSidebarMobile) {
                    ModalBottomSheet(onDismissRequest = { showSidebarMobile = false }) {
                        ModalSidebarContent(
                            categories = categories,
                            onCategorySelected = {
                                onCategorySelected(it)
                                showSidebarMobile = false
                            },
                            onSearch = {
                                onSearch(it)
                                showSidebarMobile = false
                            }
                        )
                    }
                }
            }
        }
    }
}
