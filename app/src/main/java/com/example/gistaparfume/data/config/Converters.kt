package com.example.gistaparfume.data.config

import androidx.room.TypeConverter
import com.example.gistaparfume.data.entity.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(role: String): UserRole {
        return UserRole.valueOf(role)
    }
}