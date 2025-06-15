package com.example.gistaparfume.ui
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
    widthSizeClass: WindowWidthSizeClass
) {
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact
    val isWide = widthSizeClass == WindowWidthSizeClass.Expanded

    if (isCompact) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Baris 1: Kategori kiri & Filter kanan
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
                    modifier = Modifier
                        .defaultMinSize(minHeight = 23.dp)
                        .height(23.dp)
                ) {
                    Text("Filter & Kategori", fontSize = 11.sp)
                }
            }

            // Baris 2: Urutkan Harga
            Text(
                text = "Urutkan Harga:",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Baris 3: Tombol sort (termurah + termahal) sejajar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start, // <= biar rata kiri
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onSortByPrice(false) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .defaultMinSize(minHeight = 28.dp)
                ) {
                    Text("Termurah", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { onSortByPrice(true) },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .defaultMinSize(minHeight = 28.dp)
                ) {
                    Text("Termahal", fontSize = 11.sp)
                }
            }
        }
    } else {
        // ✅ MEDIUM & TABLET (keep as is)
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
                OutlinedButton(
                    onClick = { onSortByPrice(false) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Termurah", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                OutlinedButton(
                    onClick = { onSortByPrice(true) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Termahal", fontSize = 12.sp)
                }

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
