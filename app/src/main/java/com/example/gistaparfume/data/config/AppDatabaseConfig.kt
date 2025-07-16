package com.example.gistaparfume.data.config

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gistaparfume.data.dao.CategoryDao
import com.example.gistaparfume.data.dao.ProductDao
import com.example.gistaparfume.data.dao.UserDao
import com.example.gistaparfume.data.entity.CategoryEntity
import com.example.gistaparfume.data.entity.ProductEntity
import com.example.gistaparfume.data.entity.UserEntity


@Database(
    entities = [ProductEntity::class, CategoryEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabaseConfig : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao

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
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance // The assignment happens here
                instance
            }

        private val MIGRATION_1_2 = androidx.room.migration.Migration(1, 2) {
            it.execSQL("""
                CREATE TABLE IF NOT EXISTS `user` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `email` TEXT NOT NULL,
                    `password` TEXT NOT NULL,
                    `role` TEXT NOT NULL DEFAULT 'MEMBER',
                    `is_active` INTEGER NOT NULL DEFAULT 1,
                    `image` TEXT
                )
            """.trimIndent())
        }
    }
}