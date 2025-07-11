package com.example.gistaparfume.data

data class ProductWithCategory(
    val id: Int,
    val slug: String,
    val title: String,
    val description: String,
    val price: Int,
    val isAvailable: Boolean,
    val imageRes: Int,
    val categoryTitle: String
)