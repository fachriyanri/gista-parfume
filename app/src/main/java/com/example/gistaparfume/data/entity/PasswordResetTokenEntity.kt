package com.example.gistaparfume.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for storing password reset tokens
 */
@Entity(tableName = "password_reset_token")
data class PasswordResetTokenEntity(
    @PrimaryKey
    val email: String,
    val token: String,
    val expiryTime: Long, // Timestamp when token expires
    val createdAt: Long = System.currentTimeMillis()
)