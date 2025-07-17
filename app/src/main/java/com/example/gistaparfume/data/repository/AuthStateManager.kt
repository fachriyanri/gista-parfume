package com.example.gistaparfume.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension property to create DataStore instance
private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

/**
 * Manages authentication state persistence using DataStore
 * Handles storing and retrieving user authentication information securely
 */
class AuthStateManager(private val applicationContext: Context) { // Use applicationContext to avoid memory leaks
    
    companion object {
        // DataStore preference keys
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_IS_ACTIVE_KEY = booleanPreferencesKey("user_is_active")
        private val USER_IMAGE_KEY = stringPreferencesKey("user_image")
        
        @Volatile
        private var INSTANCE: AuthStateManager? = null
        
        fun getInstance(context: Context): AuthStateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthStateManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    /**
     * Saves user authentication state to DataStore
     */
    suspend fun saveAuthState(user: UserEntity) {
        applicationContext.authDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = true
            preferences[USER_ID_KEY] = user.id
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_ROLE_KEY] = user.role.name
            preferences[USER_IS_ACTIVE_KEY] = user.isActive
            user.image?.let { preferences[USER_IMAGE_KEY] = it }
        }
    }
    
    /**
     * Retrieves the stored authentication state
     */
    suspend fun getAuthState(): UserEntity? {
        val preferences = applicationContext.authDataStore.data.first()
        val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
        
        return if (isLoggedIn) {
            val userId = preferences[USER_ID_KEY] ?: return null
            val userName = preferences[USER_NAME_KEY] ?: return null
            val userEmail = preferences[USER_EMAIL_KEY] ?: return null
            val userRoleString = preferences[USER_ROLE_KEY] ?: return null
            val userIsActive = preferences[USER_IS_ACTIVE_KEY] ?: true
            val userImage = preferences[USER_IMAGE_KEY]
            
            try {
                val userRole = UserRole.valueOf(userRoleString)
                UserEntity(
                    id = userId,
                    name = userName,
                    email = userEmail,
                    password = "", // Don't store password in preferences
                    role = userRole,
                    isActive = userIsActive,
                    image = userImage
                )
            } catch (e: IllegalArgumentException) {
                // Invalid role, clear auth state
                clearAuthState()
                null
            }
        } else {
            null
        }
    }
    
    /**
     * Checks if user is currently logged in
     */
    suspend fun isLoggedIn(): Boolean {
        val preferences = applicationContext.authDataStore.data.first()
        return preferences[IS_LOGGED_IN_KEY] ?: false
    }
    
    /**
     * Gets authentication state as a Flow for reactive updates
     */
    fun getAuthStateFlow(): Flow<UserEntity?> {
        return applicationContext.authDataStore.data.map { preferences ->
            val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
            
            if (isLoggedIn) {
                val userId = preferences[USER_ID_KEY] ?: return@map null
                val userName = preferences[USER_NAME_KEY] ?: return@map null
                val userEmail = preferences[USER_EMAIL_KEY] ?: return@map null
                val userRoleString = preferences[USER_ROLE_KEY] ?: return@map null
                val userIsActive = preferences[USER_IS_ACTIVE_KEY] ?: true
                val userImage = preferences[USER_IMAGE_KEY]
                
                try {
                    val userRole = UserRole.valueOf(userRoleString)
                    UserEntity(
                        id = userId,
                        name = userName,
                        email = userEmail,
                        password = "", // Don't store password in preferences
                        role = userRole,
                        isActive = userIsActive,
                        image = userImage
                    )
                } catch (e: IllegalArgumentException) {
                    null
                }
            } else {
                null
            }
        }
    }
    
    /**
     * Gets login status as a Flow for reactive updates
     */
    fun getLoginStatusFlow(): Flow<Boolean> {
        return applicationContext.authDataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }
    
    /**
     * Clears all authentication state from DataStore
     */
    suspend fun clearAuthState() {
        applicationContext.authDataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(USER_IS_ACTIVE_KEY)
            preferences.remove(USER_IMAGE_KEY)
        }
    }
    
    /**
     * Updates specific user information in the stored auth state
     */
    suspend fun updateUserInfo(user: UserEntity) {
        // Only update if user is currently logged in
        if (isLoggedIn()) {
            saveAuthState(user)
        }
    }
}