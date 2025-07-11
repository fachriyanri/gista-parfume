package com.example.gistaparfume.data.repository

import com.example.gistaparfume.data.Product
import com.example.gistaparfume.data.ProductWithCategory
import com.example.gistaparfume.data.dao.ProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val dao: ProductDao
) {
    private val pageSize = 2

    // normal infinite scroll
    private var currentPage = 1
    private var endReached = false

    // search infinite scroll
    private var searchKeyword: String? = null
    private var searchPage = 1
    private var searchEndReached = false

    fun resetPagination() {
        currentPage = 1
        endReached = false
    }

    fun resetSearch(keyword: String) {
        searchKeyword = keyword
        searchPage = 1
        searchEndReached = false
    }

    /** Infinite scroll biasa */
//    suspend fun loadNextPage(): List<Product> {
//        if (endReached) return emptyList()
//        val offset = (currentPage - 1) * pageSize
//        val raws = dao.getProductsWithCategory(pageSize, offset)
//        val items = raws.map { it.toProduct() }
//        if (raws.size < pageSize) endReached = true else currentPage++
//        return items
//    }

    /**
     * Infinite scroll hasil search:
     * - MATCH di title ATAU description pake LIKE '%keyword%'
     * - JOIN category
     */
//    suspend fun searchNextPage(keyword: String): List<Product> {
//        // reset kalau keyword beda
//        if (searchKeyword != keyword) resetSearch(keyword)
//        if (searchEndReached) return emptyList()
//
//        val offset = (searchPage - 1) * pageSize
//        val pattern = "%$keyword%"
//        val raws = dao.searchWithCategory(pattern, pageSize, offset)
//        val items = raws.map { it.toProduct() }
//        if (raws.size < pageSize) searchEndReached = true else searchPage++
//        return items
//    }

//    suspend fun getProductsPage(page: Int): List<Product> {
//        val offset = (page - 1) * pageSize
//        return dao.getProductsWithCategory(pageSize, offset)
//            .map { it.toProduct() }
//    }

    fun getAllProducts(): Flow<List<Product>> {
        // We'll hardcode a limit of 20 for now. Offset is 0 to start from the beginning.
        return dao.getProductsWithCategory(limit = 20, offset = 0)
            .map { list -> list.map { it.toProduct() } } // map the list to the domain model
    }

    // mapping helper
    private fun ProductWithCategory.toProduct() = Product(
        id          = id,
        title       = title,
        price       = price,
        description = description,
        category    = categoryTitle,
        imageRes    = imageRes
    )
}