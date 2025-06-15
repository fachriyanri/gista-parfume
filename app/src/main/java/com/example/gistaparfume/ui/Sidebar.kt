package com.example.gistaparfume.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Sidebar(
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xFFFF146D))
            .padding(12.dp)
    ) {
        Text("Pencarian", fontWeight = FontWeight.Bold, color = Color.Black)
        var searchText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Cari") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onSearch(searchText) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Cari")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Kategori", fontWeight = FontWeight.Bold, color = Color.Black)
        categories.forEach { category ->
            TextButton(onClick = { onCategorySelected(category) }) {
                Text(category, color = Color.Black)
            }
        }
    }
}