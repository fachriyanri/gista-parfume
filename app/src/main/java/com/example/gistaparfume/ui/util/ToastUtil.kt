package com.example.gistaparfume.ui.util

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Utility object for showing toast messages
 */
object ToastUtil {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }
    
    fun showLongToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

/**
 * Composable function to show toast messages reactively
 */
@Composable
fun ShowToast(
    message: String?,
    isLongDuration: Boolean = false,
    onToastShown: () -> Unit = {}
) {
    val context = LocalContext.current
    
    LaunchedEffect(message) {
        message?.let {
            if (isLongDuration) {
                ToastUtil.showLongToast(context, it)
            } else {
                ToastUtil.showToast(context, it)
            }
            onToastShown()
        }
    }
}