package com.example.gistaparfume.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Utility object for accessibility enhancements
 */
object AccessibilityUtil {
    
    /**
     * Creates a content description for form fields
     */
    fun createFieldDescription(
        fieldName: String,
        value: String,
        isRequired: Boolean = false,
        hasError: Boolean = false,
        errorMessage: String? = null
    ): String {
        val parts = mutableListOf<String>()
        
        parts.add(fieldName)
        
        if (isRequired) {
            parts.add("wajib diisi")
        }
        
        if (value.isNotEmpty()) {
            parts.add("berisi: $value")
        } else {
            parts.add("kosong")
        }
        
        if (hasError && errorMessage != null) {
            parts.add("error: $errorMessage")
        }
        
        return parts.joinToString(", ")
    }
    
    /**
     * Creates a content description for buttons
     */
    fun createButtonDescription(
        buttonText: String,
        isEnabled: Boolean = true,
        isLoading: Boolean = false,
        additionalInfo: String? = null
    ): String {
        val parts = mutableListOf<String>()
        
        parts.add("Tombol $buttonText")
        
        when {
            isLoading -> parts.add("sedang memproses")
            !isEnabled -> parts.add("tidak aktif")
            else -> parts.add("dapat ditekan")
        }
        
        additionalInfo?.let { parts.add(it) }
        
        return parts.joinToString(", ")
    }
    
    /**
     * Creates a content description for list items
     */
    fun createListItemDescription(
        itemName: String,
        itemDetails: String,
        position: Int,
        totalItems: Int,
        hasActions: Boolean = false
    ): String {
        val parts = mutableListOf<String>()
        
        parts.add("Item $position dari $totalItems")
        parts.add(itemName)
        parts.add(itemDetails)
        
        if (hasActions) {
            parts.add("dengan tombol aksi")
        }
        
        return parts.joinToString(", ")
    }
}

/**
 * Accessible OutlinedTextField with proper keyboard navigation and read-only support
 */
@Composable
fun AccessibleOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    isRequired: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = isError,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = AccessibilityUtil.createFieldDescription(
                    fieldName = label,
                    value = value,
                    isRequired = isRequired,
                    hasError = isError,
                    errorMessage = errorMessage
                )
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (enabled) {
                    focusManager.moveFocus(FocusDirection.Down)
                    onImeAction()
                }
            },
            onDone = {
                if (enabled) {
                    focusManager.clearFocus()
                    onImeAction()
                }
            },
            onSearch = {
                if (enabled) {
                    focusManager.clearFocus()
                    onImeAction()
                }
            }
        )
    )
}