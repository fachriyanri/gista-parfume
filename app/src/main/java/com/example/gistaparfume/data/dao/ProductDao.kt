package com.example.gistaparfume.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gistaparfume.data.entity.ProductEntity
import com.example.gistaparfume.data.ProductWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Transaction
    @Query("""
    SELECT p.id, p.slug, p.title, p.description, p.price, p.isAvailable, p.imageRes,
           c.title AS categoryTitle
    FROM product p
    JOIN category c ON p.idCategory = c.id
    LIMIT :limit OFFSET :offset
  """)
    fun getProductsWithCategory(limit: Int, offset: Int): Flow<List<ProductWithCategory>>

    @Transaction
    @Query("""
    SELECT p.id, p.slug, p.title, p.description, p.price, p.isAvailable, p.imageRes,
           c.title AS categoryTitle
    FROM product p
    JOIN category c ON p.idCategory = c.id
    WHERE p.title LIKE :keyword OR p.description LIKE :keyword
    LIMIT :limit OFFSET :offset
  """)
    suspend fun searchWithCategory(keyword: String, limit: Int, offset: Int): List<ProductWithCategory>

    @Transaction
    @Query("""
    SELECT p.id, p.slug, p.title, p.description, p.price, p.isAvailable, p.imageRes,
           c.title AS categoryTitle
    FROM product p
    JOIN category c ON p.idCategory = c.id
    WHERE 
        (p.title LIKE :query OR p.description LIKE :query) 
        AND
        (:categoryTitle = 'All' OR c.title = :categoryTitle)
    LIMIT :limit OFFSET :offset
""")
    suspend fun getProducts(
        query: String,
        categoryTitle: String,
        limit: Int,
        offset: Int
    ): List<ProductWithCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)
}