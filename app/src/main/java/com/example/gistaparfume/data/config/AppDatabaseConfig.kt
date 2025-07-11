package com.example.gistaparfume.data.config

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.dao.ProductDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.entity.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.gistaparfume.R
import java.util.concurrent.Executors


@Database(
    entities = [ProductEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabaseConfig : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseConfig? = null

        fun getDatabase(context: Context): AppDatabaseConfig =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseConfig::class.java,
                    "app.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Use a one-off executor to run the insertion after the
                            // database instance is fully created and assigned.
                            Log.d("DB_INIT", "onCreate callback triggered. Attempting to insert data.")

                            Executors.newSingleThreadExecutor().execute {
                                INSTANCE?.let { database ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        // dummy categories
                                        val cats = listOf(
                                            CategoryEntity(1, "extrait-de-parfum", "Extrait De Parfum"),
                                            CategoryEntity(2, "eau-de-parfume", "Eau De Parfume"),
                                            CategoryEntity(3, "eau-de-cologne", "Eau De Cologne")
                                        )
                                        database.categoryDao().insertAll(cats) // Assuming you have an insertAll for categories

                                        // dummy products
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
                                                description = "Parfum Pria",
                                                price = 100000,
                                                isAvailable = true,
                                                imageRes = R.drawable.dioresimo_de_parfume,
                                                idCategory = 2
                                            )
                                        )
                                        database.productDao().insertAll(prods)
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance // The assignment happens here
                instance
            }
    }
}