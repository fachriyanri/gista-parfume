package com.example.gistaparfume.data

import com.example.gistaparfume.data.entity.CategoryEntity

// UI model
data class Category(
    val id: Int,
    val title: String
)

// extension mapper dari Entity → UI
fun CategoryEntity.toUI(): Category = Category(
    id    = this.id,
    title = this.title
)
