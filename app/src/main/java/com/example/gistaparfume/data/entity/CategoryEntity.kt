package com.example.gistaparfume.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val title: String
)