package com.example.gistaparfume.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.viewmodel.AddCategoryViewModel
import com.example.gistaparfume.ui.util.AccessibilityUtil
import com.example.gistaparfume.ui.util.AccessibleOutlinedTextField
import com.example.gistaparfume.ui.util.ResponsiveUtil
import com.example.gistaparfume.ui.util.ShowToast

@Composable
fun AddCategoryScreen(
    widthSizeClass: WindowWidthSizeClass,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToCategoryManagement: () -> Unit,
    onUserManagementClick: () -> Unit,
    currentUser: UserEntity? = null,
    isLoggedIn: Boolean = false,
    onProfileClick: () -> Unit,
    viewModel: AddCategoryViewModel = viewModel()
) {
    val formState by viewModel.formState.collectAsState()
    
    // Success dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var hasShownSuccessDialog by remember { mutableStateOf(false) }
    
    // Toast message state
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
    // Responsive design values
    val responsivePadding = ResponsiveUtil.getResponsivePadding(widthSizeClass)
    val responsiveFormWidth = ResponsiveUtil.getResponsiveFormWidth(widthSizeClass)

    // Handle logout navigation
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onNavigateToHome()
        }
    }

    // Handle form submission success
    LaunchedEffect(formState.isOperationSuccessful) {
        if (formState.isOperationSuccessful && !hasShownSuccessDialog) {
            showSuccessDialog = true
            hasShownSuccessDialog = true
        }
    }

    // Reset success dialog tracking when form is reset
    LaunchedEffect(formState.title, formState.slug) {
        if (formState.title.isEmpty() && formState.slug.isEmpty()) {
            hasShownSuccessDialog = false
        }
    }

    // Handle error messages with toast
    LaunchedEffect(formState.errorMessage) {
        formState.errorMessage?.let { message ->
            if (message.isNotEmpty()) {
                toastMessage = message
            }
        }
    }

    // Show toast messages
    ShowToast(
        message = toastMessage,
        onToastShown = { toastMessage = null }
    )

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Prevent dismissal by clicking outside */ },
            title = { Text("Berhasil") },
            text = { Text("Kategori berhasil ditambahkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        hasShownSuccessDialog = false
                        viewModel.resetFormAfterSuccess()
                        onNavigateToCategoryManagement()
                    }
                ) {
                    Text("OK", color = Color(0xFF990000))
                }
            }
        )
    }

    when (widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            // ===== TABLET / DESKTOP =====
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TopHeader(
                        widthSizeClass = widthSizeClass,
                        onHomeClick = onNavigateToHome,
                        currentUser = currentUser,
                        isLoggedIn = isLoggedIn,
                        onLogout = onLogout,
                        onUserManagementClick = onUserManagementClick,
                        onCategoryManagementClick = onNavigateToCategoryManagement,
                        onProfileClick = onProfileClick
                    )
                    HorizontalDivider(color = Color.White, thickness = 7.dp)

                    // Main content area with centered form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        AddCategoryForm(
                            formState = formState,
                            onTitleChange = viewModel::updateTitle,
                            onSaveCategory = { viewModel.saveCategory() },
                            modifier = Modifier
                                .widthIn(max = 600.dp)
                                .padding(horizontal = 32.dp, vertical = 24.dp)
                        )
                    }
                }
            }
        }
        WindowWidthSizeClass.Medium -> {
            // ===== MEDIUM PHONE =====
            Column(modifier = Modifier.fillMaxSize()) {
                TopHeader(
                    widthSizeClass = widthSizeClass,
                    onHomeClick = onNavigateToHome,
                    currentUser = currentUser,
                    isLoggedIn = isLoggedIn,
                    onLogout = onLogout,
                    onUserManagementClick = onUserManagementClick,
                    onCategoryManagementClick = onNavigateToCategoryManagement,
                    onProfileClick = onProfileClick
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)

                // Form with optimized spacing
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    AddCategoryForm(
                        formState = formState,
                        onTitleChange = viewModel::updateTitle,
                        onSaveCategory = { viewModel.saveCategory() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    )
                }
            }
        }
        else -> {
            // ===== COMPACT / MOBILE =====
            Column(modifier = Modifier.fillMaxSize()) {
                TopHeader(
                    widthSizeClass = widthSizeClass,
                    onHomeClick = onNavigateToHome,
                    currentUser = currentUser,
                    isLoggedIn = isLoggedIn,
                    onLogout = onLogout,
                    onUserManagementClick = onUserManagementClick,
                    onCategoryManagementClick = onNavigateToCategoryManagement,
                    onProfileClick = onProfileClick
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)

                // Form with mobile spacing
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    AddCategoryForm(
                        formState = formState,
                        onTitleChange = viewModel::updateTitle,
                        onSaveCategory = { viewModel.saveCategory() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryForm(
    formState: com.example.gistaparfume.data.CategoryFormState,
    onTitleChange: (String) -> Unit,
    onSaveCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Formulir Kategori",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF990000),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Category Name Input Field
        AccessibleOutlinedTextField(
            value = formState.title,
            onValueChange = onTitleChange,
            label = "Kategori",
            placeholder = "Masukkan nama kategori",
            isError = formState.titleError != null,
            errorMessage = formState.titleError,
            isRequired = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message for title
        if (formState.titleError != null) {
            Text(
                text = formState.titleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        // Slug Display Field (Read-only, Auto-generated)
        AccessibleOutlinedTextField(
            value = formState.slug,
            onValueChange = { /* Read-only field - no changes allowed */ },
            label = "Slug (Otomatis)",
            placeholder = "Slug akan dibuat otomatis dari nama kategori",
            isError = formState.slugError != null,
            errorMessage = formState.slugError,
            isRequired = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
            onImeAction = { onSaveCategory() },
            enabled = false, // Make field read-only
            modifier = Modifier.fillMaxWidth()
        )
        
        // Supporting text for slug
        Text(
            text = "Slug dibuat otomatis dari nama kategori untuk menghindari kesalahan format",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
        
        // Error message for slug
        if (formState.slugError != null) {
            Text(
                text = formState.slugError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        // General error message display
        if (formState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = formState.errorMessage,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Save Button
        SaveCategoryButton(
            isEnabled = formState.isFormValid && !formState.isLoading,
            isLoading = formState.isLoading,
            onClick = onSaveCategory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}



@Composable
private fun SaveCategoryButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = modifier
            .height(48.dp)
            .semantics {
                contentDescription = AccessibilityUtil.createButtonDescription(
                    buttonText = "Simpan kategori",
                    isEnabled = isEnabled,
                    isLoading = isLoading,
                    additionalInfo = if (isLoading) "Sedang menyimpan kategori" else "Simpan kategori baru"
                )
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF990000),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "Simpan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}