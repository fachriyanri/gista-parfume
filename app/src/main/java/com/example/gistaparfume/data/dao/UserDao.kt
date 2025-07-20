package com.example.gistaparfume.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gistaparfume.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long
    
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    // New methods for user management operations
    @Query("SELECT * FROM user ORDER BY name COLLATE NOCASE ASC LIMIT :limit OFFSET :offset")
    suspend fun getAllUsersPaginated(limit: Int, offset: Int): List<UserEntity>

    @Query("SELECT * FROM user WHERE name LIKE :query OR email LIKE :query ORDER BY name COLLATE NOCASE ASC LIMIT :limit OFFSET :offset")
    suspend fun searchUsers(query: String, limit: Int, offset: Int): List<UserEntity>

    @Query("SELECT * FROM user WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?
    
    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: Int)
    
    @Query("SELECT COUNT(*) FROM user")
    suspend fun getTotalUserCount(): Int
    
    @Query("SELECT COUNT(*) FROM user WHERE name LIKE :query OR email LIKE :query")
    suspend fun getSearchResultCount(query: String): Int
}