package com.example.gistaparfume.data.repository

import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.toUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryRepository(
    private val dao: CategoryDao
) {
    private val perPage = 5

    suspend fun getAllCategories(): List<CategoryEntity> =
        dao.getAll()

    /**
     * SEARCH kategori by title (LIKE '%keyword%') + paginate
     */
    suspend fun searchCategories(keyword: String, page: Int): List<Category> = withContext(Dispatchers.IO) {
        val pattern = "%$keyword%"
        // ambil semua hasil LIKE
        val allMatches: List<CategoryEntity> = dao.search(pattern)
        // terapkan pagination manual
        val offset = (page - 1) * perPage
        allMatches
            .drop(offset)
            .take(perPage)
            .map(CategoryEntity::toUI)
    }

    /**
     * COUNT total kategori,
     * kalo keyword null pake total semu, kalo ada keyword pake size search
     */
    suspend fun countCategories(keyword: String? = null): Int = withContext(Dispatchers.IO) {
        if (keyword.isNullOrBlank()) {
            dao.count()
        } else {
            dao.search("%$keyword%").size
        }
    }
}