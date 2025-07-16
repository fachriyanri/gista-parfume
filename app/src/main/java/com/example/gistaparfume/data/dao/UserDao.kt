package com.example.gistaparfume.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gistaparfume.data.entity.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long
    
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?
}