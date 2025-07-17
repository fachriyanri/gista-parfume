package com.example.gistaparfume.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gistaparfume.data.entity.PasswordResetTokenEntity

@Dao
interface PasswordResetTokenDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: PasswordResetTokenEntity)
    
    @Query("SELECT * FROM password_reset_token WHERE email = :email LIMIT 1")
    suspend fun getTokenByEmail(email: String): PasswordResetTokenEntity?
    
    @Query("DELETE FROM password_reset_token WHERE email = :email")
    suspend fun deleteTokenByEmail(email: String)
    
    @Query("DELETE FROM password_reset_token WHERE expiryTime < :currentTime")
    suspend fun deleteExpiredTokens(currentTime: Long)
}