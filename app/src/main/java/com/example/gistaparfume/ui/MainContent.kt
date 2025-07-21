package com.example.gistaparfume.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gistaparfume.data.Product

@Composable
fun MainContent(
    products: List<Product>,
    isLoading: Boolean,               // 1. Add isLoading to the parameters
    onLoadMore: () -> Unit,           // 2. Add the onLoadMore event
    onAddToCart: (Product, Int) -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {

    // 3. Create and remember the state of the grid to track scrolling
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        state = gridState, // <-- Assign the state here
        modifier = modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp) ,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onAddToCart = onAddToCart,
                widthSizeClass = widthSizeClass
            )
        }

        // 5. Add a loading spinner at the bottom when isLoading is true
        if (isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) { // Make the item span all columns
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // 4. This is the "brain" that detects when to load more
    LaunchedEffect(gridState, isLoading) {
        // Create a flow that emits the latest visible item info
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull() ?: return@collect
                Log.d("GRID_TRACE", "Visible last item: ${lastVisibleItem.index}, Total: ${gridState.layoutInfo.totalItemsCount}")

                // This logic checks if the user has scrolled near the end of the list
                // and if we are not currently loading new data.
                val isNearEnd = lastVisibleItem.index >= products.size - 3
                if (isNearEnd && !isLoading) {
                    onLoadMore() // Trigger the function to load the next page
                }
            }
    }
}