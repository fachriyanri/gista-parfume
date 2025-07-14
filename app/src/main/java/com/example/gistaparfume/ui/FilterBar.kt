package com.example.gistaparfume.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterBar(
    selectedCategory: String?,
    onSortByPrice: (Boolean) -> Unit,
    onFilterClick: () -> Unit,
    widthSizeClass: WindowWidthSizeClass,
    sortDescending: Boolean?
) {
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact
    val isWide = widthSizeClass == WindowWidthSizeClass.Expanded

    // Determine which sort button is currently active.
    val isTermurahActive = sortDescending == false
    val isTermahalActive = sortDescending == true

    if (isCompact) {
        // Layout for Compact screens (e.g., mobile portrait)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Category and Filter button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kategori: ${selectedCategory ?: "-"}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = onFilterClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(23.dp)
                ) {
                    Text("Filter & Kategori", fontSize = 11.sp)
                }
            }

            // Row 2: "Urutkan Harga" Label
            Text(
                text = "Urutkan Harga:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Row 3: Sort buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Use the refactored SortButton composable
                SortButton(
                    text = "Termurah",
                    isActive = isTermurahActive,
                    onClick = { onSortByPrice(false) },
                    modifier = Modifier.height(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SortButton(
                    text = "Termahal",
                    isActive = isTermahalActive,
                    onClick = { onSortByPrice(true) },
                    modifier = Modifier.height(28.dp)
                )
            }
        }
    } else {
        // Layout for Medium and Expanded screens (e.g., tablets, landscape phones)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Kategori: ${selectedCategory ?: "-"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Urutkan Harga:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Use the refactored SortButton composable
                SortButton(
                    text = "Termurah",
                    isActive = isTermurahActive,
                    onClick = { onSortByPrice(false) },
                    modifier = Modifier.height(32.dp),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                SortButton(
                    text = "Termahal",
                    isActive = isTermahalActive,
                    onClick = { onSortByPrice(true) },
                    modifier = Modifier.height(32.dp),
                    fontSize = 12.sp
                )

                if (!isWide) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = onFilterClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Filter & Kategori", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


@Composable
private fun SortButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 11.sp
) {
    val contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)

    if (isActive) {
        // Show a filled Button when the sort is active
        Button(
            onClick = onClick,
            modifier = modifier,
            contentPadding = contentPadding
        ) {
            Text(text, fontSize = fontSize)
        }
    } else {
        // Show an OutlinedButton when the sort is inactive
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = contentPadding
        ) {
            Text(text, fontSize = fontSize)
        }
    }
}
