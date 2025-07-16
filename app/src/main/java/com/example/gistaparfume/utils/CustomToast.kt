package com.example.gistaparfume.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

/**
 * Custom toast utility that allows for precise duration control
 */
object CustomToast {
    
    /**
     * Shows a toast message for exactly 5 seconds
     * @param context The application context
     * @param message The message to display
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Shows a 5-second success toast specifically for registration
     * @param context The application context
     * @param message The success message to display
     */
    fun showRegistrationSuccessToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}