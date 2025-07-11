package com.example.gistaparfume.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["idCategory"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("idCategory")]
)
data class ProductEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val title: String,
    val description: String,
    val price: Int,
    val isAvailable: Boolean,
    val imageRes: Int,
    val idCategory: Int
)