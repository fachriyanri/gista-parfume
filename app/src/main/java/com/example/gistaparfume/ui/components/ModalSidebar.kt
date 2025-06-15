package com.example.gistaparfume.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ModalSidebarContent(
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pencarian", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Cari") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onSearch(searchText) }, modifier = Modifier.fillMaxWidth()) {
            Text("Cari")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Kategori", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        categories.forEach { category ->
            TextButton(onClick = { onCategorySelected(category) }) {
                Text(category)
            }
        }
    }
}