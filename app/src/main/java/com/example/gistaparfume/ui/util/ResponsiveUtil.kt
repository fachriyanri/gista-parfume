package com.example.gistaparfume.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utility object for responsive design calculations
 */
object ResponsiveUtil {
    
    /**
     * Get responsive padding based on screen size
     */
    fun getResponsivePadding(widthSizeClass: WindowWidthSizeClass): PaddingValues {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            WindowWidthSizeClass.Medium -> PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            WindowWidthSizeClass.Expanded -> PaddingValues(horizontal = 32.dp, vertical = 24.dp)
            else -> PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        }
    }
    
    /**
     * Get responsive horizontal padding
     */
    fun getResponsiveHorizontalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 16.dp
            WindowWidthSizeClass.Medium -> 24.dp
            WindowWidthSizeClass.Expanded -> 32.dp
            else -> 16.dp
        }
    }
    
    /**
     * Get responsive vertical padding
     */
    fun getResponsiveVerticalPadding(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 12.dp
            WindowWidthSizeClass.Medium -> 16.dp
            WindowWidthSizeClass.Expanded -> 24.dp
            else -> 12.dp
        }
    }
    
    /**
     * Get responsive form width
     */
    fun getResponsiveFormWidth(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> Dp.Unspecified
            WindowWidthSizeClass.Medium -> 500.dp
            WindowWidthSizeClass.Expanded -> 600.dp
            else -> Dp.Unspecified
        }
    }
    
    /**
     * Get responsive spacing between elements
     */
    fun getResponsiveSpacing(widthSizeClass: WindowWidthSizeClass): Dp {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 12.dp
            WindowWidthSizeClass.Medium -> 16.dp
            WindowWidthSizeClass.Expanded -> 20.dp
            else -> 12.dp
        }
    }
    
    /**
     * Get responsive font size multiplier
     */
    fun getResponsiveFontSizeMultiplier(widthSizeClass: WindowWidthSizeClass): Float {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 1.0f
            WindowWidthSizeClass.Medium -> 1.1f
            WindowWidthSizeClass.Expanded -> 1.2f
            else -> 1.0f
        }
    }
    
    /**
     * Check if device is in landscape mode
     */
    @Composable
    fun isLandscape(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp > configuration.screenHeightDp
    }
    
    /**
     * Get responsive column count for grids
     */
    fun getResponsiveColumnCount(widthSizeClass: WindowWidthSizeClass): Int {
        return when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 1
            WindowWidthSizeClass.Medium -> 2
            WindowWidthSizeClass.Expanded -> 3
            else -> 1
        }
    }
}