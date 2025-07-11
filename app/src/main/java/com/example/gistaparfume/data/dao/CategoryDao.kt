package com.example.gistaparfume.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gistaparfume.data.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getById(id: Int): CategoryEntity?

    @Query("SELECT * FROM category WHERE slug = :slug")
    suspend fun getBySlug(slug: String): CategoryEntity?

    @Query("SELECT * FROM category WHERE title LIKE :pattern")
    suspend fun searchByTitle(pattern: String): List<CategoryEntity>

    @Query("""
      SELECT * FROM category 
      WHERE slug LIKE :pattern OR title LIKE :pattern
    """)
    suspend fun search(pattern: String): List<CategoryEntity>

    @Query("""
      SELECT * FROM category 
      ORDER BY title ASC 
      LIMIT :limit OFFSET :offset
    """)
    suspend fun getPaged(limit: Int, offset: Int): List<CategoryEntity>

    // Tambahan:
    @Query("SELECT COUNT(*) FROM category")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity): Int

    @Delete
    suspend fun delete(category: CategoryEntity): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(category: List<CategoryEntity>)
}