package com.example.gistaparfume.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.gistaparfume.R
import com.example.gistaparfume.data.Product
import com.example.gistaparfume.data.ProductWithCategory
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.dao.ProductDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.entity.ProductEntity
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import com.example.gistaparfume.utils.PasswordUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val context: Context
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

    suspend fun setupDatabaseIfNeeded() {
        val dataSeededKey = booleanPreferencesKey("is_data_seeded")
        // This reads the flag from DataStore to see if we've run this before.
        val isSeeded = context.dataStore.data.first()[dataSeededKey] ?: false

        // Only run the insertion logic if the flag is false.
        if (!isSeeded) {
            // Data has not been seeded, so insert it now.
            val cats = listOf(
                CategoryEntity(1, "extrait-de-parfum", "Extrait De Parfum"),
                CategoryEntity(2, "eau-de-parfume", "Eau De Parfume"),
                CategoryEntity(3, "eau-de-cologne", "Eau De Cologne")
            )
            // Note: I'm calling categoryDao() and productDao() through the main 'dao' property
            // assuming they are both available from the AppDatabaseConfig.
            // If not, you may need to adjust how you access them.
            // Based on your code, you pass the single 'productDao', but you need 'categoryDao' too.
            // We'll adjust this in the ViewModel call.

            // This is your actual category list
            val categoryDao = AppDatabaseConfig.getDatabase(context).categoryDao()
            categoryDao.insertAll(cats)

            // This is your actual product list
            val prods = listOf(
                ProductEntity(
                    id = 1,
                    slug = "bleau-de-channel",
                    title = "Bleau De Channel",
                    description = "Parfum Pria",
                    price = 250000,
                    isAvailable = true,
                    imageRes = R.drawable.bleau_de_channel,
                    idCategory = 1
                ),
                ProductEntity(
                    id = 2,
                    slug = "dioresimo-de-parfume",
                    title = "Dioresimo De Parfume",
                    description = "Parfum Wanita",
                    price = 100000,
                    isAvailable = true,
                    imageRes = R.drawable.dioresimo_de_parfume,
                    idCategory = 2
                ),
                ProductEntity(
                    id = 3,
                    slug = "blvgari-man-rain-essence",
                    title = "Blvgari Man Rain Essence",
                    description = "Parfum Pria",
                    price = 150000,
                    isAvailable = true,
                    imageRes = R.drawable.blvgari_man_rain_essence,
                    idCategory = 3
                ),
                ProductEntity(
                    id = 4,
                    slug = "miss-dior-blooming-bouquet",
                    title = "Miss Dior Blooming Bouquet",
                    description = "Parfum Wanita",
                    price = 550000,
                    isAvailable = true,
                    imageRes = R.drawable.miss_dior_blooming_bouquet,
                    idCategory = 3
                )
            )
            productDao.insertAll(prods) // 'dao' here is the productDao passed to the repository

            // Create admin user during initial setup
            val userDao = AppDatabaseConfig.getDatabase(context).userDao()
            
            // Check if admin user already exists to avoid duplicates
            val existingAdmin = userDao.findByEmail("admin@mail.com")
            if (existingAdmin == null) {
                // Encrypt the admin password
                val encryptedPassword = PasswordUtils.encryptPassword("@dminGista")
                
                // Create admin user entity
                val adminUser = UserEntity(
                    name = "admin",
                    email = "admin@mail.com",
                    password = encryptedPassword,
                    role = UserRole.ADMIN,
                    isActive = true
                )
                
                // Insert admin user into database
                userDao.insertUser(adminUser)
            }

            // Mark that the data has been seeded so this block never runs again.
            context.dataStore.edit { settings ->
                settings[dataSeededKey] = true
            }
        }
    }

    suspend fun getProductsPage(
        page: Int,
        query: String,
        category: String,
        isSortDesc: Boolean?
    ): List<Product> {
        val offset = (page - 1) * pageSize
        val pattern = "%$query%" // Add wildcards for LIKE search
        return productDao.getProducts(pattern, category, pageSize, offset,isSortDesc = isSortDesc)
            .map { it.toProduct() }
    }


    fun getAllProducts(): Flow<List<Product>> {
        // We'll hardcode a limit of 20 for now. Offset is 0 to start from the beginning.
        return productDao.getProductsWithCategory(limit = 20, offset = 0)
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