package com.example.gistaparfume.utils

import android.content.Context
import android.util.Log
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import com.example.gistaparfume.data.repository.AuthStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class for testing authentication state persistence
 * This can be used to verify that the AuthStateManager is working correctly
 */
object AuthTestUtils {
    
    /**
     * Tests the authentication state persistence functionality
     */
    fun testAuthStatePersistence(context: Context) {
        val scope = CoroutineScope(Dispatchers.Main)
        
        scope.launch {
            try {
                val authStateManager = AuthStateManager.getInstance(context)
                
                // Test user data
                val testUser = UserEntity(
                    id = 1,
                    name = "Test User",
                    email = "test@example.com",
                    password = "encrypted_password",
                    role = UserRole.MEMBER,
                    isActive = true,
                    image = null
                )
                
                Log.d("AuthTestUtils", "Testing authentication state persistence...")
                
                // Test 1: Save auth state
                Log.d("AuthTestUtils", "Test 1: Saving auth state")
                authStateManager.saveAuthState(testUser)
                
                // Test 2: Check if logged in
                val isLoggedIn = authStateManager.isLoggedIn()
                Log.d("AuthTestUtils", "Test 2: Is logged in: $isLoggedIn")
                
                // Test 3: Retrieve auth state
                val retrievedUser = authStateManager.getAuthState()
                Log.d("AuthTestUtils", "Test 3: Retrieved user: ${retrievedUser?.email}")
                
                // Test 4: Clear auth state
                Log.d("AuthTestUtils", "Test 4: Clearing auth state")
                authStateManager.clearAuthState()
                
                // Test 5: Check if logged in after clear
                val isLoggedInAfterClear = authStateManager.isLoggedIn()
                Log.d("AuthTestUtils", "Test 5: Is logged in after clear: $isLoggedInAfterClear")
                
                // Test 6: Try to retrieve auth state after clear
                val retrievedUserAfterClear = authStateManager.getAuthState()
                Log.d("AuthTestUtils", "Test 6: Retrieved user after clear: ${retrievedUserAfterClear?.email ?: "null"}")
                
                Log.d("AuthTestUtils", "Authentication state persistence test completed successfully!")
                
            } catch (e: Exception) {
                Log.e("AuthTestUtils", "Error testing authentication state persistence", e)
            }
        }
    }
    
    /**
     * Logs the current authentication state
     */
    fun logCurrentAuthState(context: Context) {
        val scope = CoroutineScope(Dispatchers.Main)
        
        scope.launch {
            try {
                val authStateManager = AuthStateManager.getInstance(context)
                val isLoggedIn = authStateManager.isLoggedIn()
                val currentUser = authStateManager.getAuthState()
                
                Log.d("AuthTestUtils", "=== Current Authentication State ===")
                Log.d("AuthTestUtils", "Is Logged In: $isLoggedIn")
                Log.d("AuthTestUtils", "Current User: ${currentUser?.email ?: "None"}")
                Log.d("AuthTestUtils", "User Role: ${currentUser?.role?.name ?: "None"}")
                Log.d("AuthTestUtils", "User Active: ${currentUser?.isActive ?: "N/A"}")
                Log.d("AuthTestUtils", "=====================================")
                
            } catch (e: Exception) {
                Log.e("AuthTestUtils", "Error logging current auth state", e)
            }
        }
    }
}