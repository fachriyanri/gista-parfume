package com.example.gistaparfume.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.dao.ProductDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.entity.ProductEntity


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
                    .build()
                INSTANCE = instance // The assignment happens here
                instance
            }
    }
}