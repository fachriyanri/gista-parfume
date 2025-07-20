package com.example.gistaparfume.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.viewmodel.CategoryManagementViewModel
import com.example.gistaparfume.ui.util.AccessibilityUtil
import com.example.gistaparfume.ui.util.ResponsiveUtil
import com.example.gistaparfume.ui.util.ShowToast
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun CategoryManagementScreen(
    widthSizeClass: WindowWidthSizeClass,
    onHomeClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onAddCategoryClick: () -> Unit = {},
    onEditCategoryClick: (Int) -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    currentUser: UserEntity? = null,
    isLoggedIn: Boolean = false,
    viewModel: CategoryManagementViewModel = viewModel(),
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    
    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    
    // Toast message state
    var toastMessage by remember { mutableStateOf<String?>(null) }
    
    // Responsive design values
    val responsivePadding = ResponsiveUtil.getResponsivePadding(widthSizeClass)
    val responsiveSpacing = ResponsiveUtil.getResponsiveSpacing(widthSizeClass)
    val isLandscape = ResponsiveUtil.isLandscape()

    // Initialize data loading
    LaunchedEffect(Unit) {
        viewModel.resetAndLoad()
    }

    // Handle logout navigation
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onHomeClick()
        }
    }

    // Handle error toast messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
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

    // Infinite scroll detection
    LaunchedEffect(listState, uiState.isLoading) {
        snapshotFlow {
            // Ambil state yang relevan untuk pagination
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 // Gunakan -1 jika kosong

            // LOGGING: Tambahkan log untuk melihat state saat ini
            Log.d(
                "PAGINATION_TRACE",
                "LastVisible: $lastVisibleItemIndex, TotalItems: $totalItemsCount, IsLoading: ${uiState.isLoading}"
            )

            // KONDISI BARU: Trigger HANYA jika item terakhir terlihat
            // Ini adalah praktik standar untuk pagination agar tidak terlalu agresif.
            // Kita juga pastikan ada item di daftar sebelum memeriksa.
            if (totalItemsCount > 0 && !uiState.isLoading) {
                lastVisibleItemIndex >= totalItemsCount - 1
            } else {
                false
            }
        }
            .distinctUntilChanged() // Hanya proses jika hasil kondisinya (true/false) berubah
            .filter { it } // Hanya lanjutkan jika kondisinya true (yaitu, kita harus memuat lebih banyak)
            .collect {
                Log.d("PAGINATION_TRACE", ">>> TRIGGERING loadNextPage...")
                viewModel.loadNextPage()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header
        TopHeader(
            widthSizeClass = widthSizeClass,
            onHomeClick = onHomeClick,
            currentUser = currentUser,
            isLoggedIn = isLoggedIn,
            onLogout = onLogout,
            onUserManagementClick = onUserManagementClick,
            onCategoryManagementClick = {}, // Already on category management screen
            onProfileClick = onProfileClick
        )

        HorizontalDivider(color = Color.White, thickness = 7.dp)

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(responsivePadding)
        ) {
            // Page Title
            Text(
                text = "Manajemen Kategori",
                fontSize = (24 * ResponsiveUtil.getResponsiveFontSizeMultiplier(widthSizeClass)).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF990000),
                modifier = Modifier
                    .padding(bottom = responsiveSpacing)
                    .semantics {
                        contentDescription = "Halaman Manajemen Kategori"
                    }
            )

            // Search Bar and Add Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = responsiveSpacing),
                horizontalArrangement = if (isLandscape && widthSizeClass != WindowWidthSizeClass.Compact) 
                    Arrangement.spacedBy(responsiveSpacing) else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Cari kategori...") },
                    modifier = Modifier
                        .weight(1f)
                        .semantics {
                            contentDescription = AccessibilityUtil.createFieldDescription(
                                fieldName = "Pencarian kategori",
                                value = searchText,
                                isRequired = false,
                                hasError = false
                            )
                        },
                    trailingIcon = {
                        Row {
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                        viewModel.resetFilters()
                                        keyboardController?.hide()
                                    },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Hapus pencarian dan tampilkan semua kategori"
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Hapus pencarian",
                                        tint = Color(0xFF990000)
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onFilterChanged(searchText)
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "Cari kategori dengan kata kunci: $searchText"
                                }
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Cari",
                                    tint = Color(0xFF990000)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.onFilterChanged(searchText)
                            keyboardController?.hide()
                        }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(responsiveSpacing))

                // Add Category Button
                Button(
                    onClick = onAddCategoryClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF990000)
                    ),
                    modifier = Modifier
                        .height(56.dp)
                        .semantics {
                            contentDescription = AccessibilityUtil.createButtonDescription(
                                buttonText = "Tambah kategori baru",
                                isEnabled = true,
                                isLoading = false,
                                additionalInfo = "Navigasi ke halaman formulir kategori"
                            )
                        }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null, // Handled by button semantics
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tambah",
                        color = Color.White,
                        fontSize = (14 * ResponsiveUtil.getResponsiveFontSizeMultiplier(widthSizeClass)).sp
                    )
                }
            }

            // Error Message
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Category List Section
            if (uiState.categories.isEmpty() && !uiState.isLoading) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.isSearchActive) {
                                "Tidak ada kategori yang ditemukan untuk pencarian \"${uiState.searchQuery}\""
                            } else {
                                "Belum ada kategori"
                            },
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.isSearchActive) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    searchText = ""
                                    viewModel.resetFilters()
                                }
                            ) {
                                Text(
                                    "Tampilkan semua kategori",
                                    color = Color(0xFF990000)
                                )
                            }
                        }
                    }
                }
            } else {
                // Category Name subtitle
                Text(
                    text = "Category Name",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Category List with infinite scroll
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.categories) { index, category ->
                        CategoryCard(
                            category = category,
                            onEditClick = { onEditCategoryClick(category.id) },
                            onDeleteClick = {
                                categoryToDelete = category
                                showDeleteDialog = true
                            },
                            isDeleting = uiState.isDeleting,
                            position = index,
                            totalItems = uiState.categories.size
                        )
                    }

                    // Loading indicator at bottom during pagination
                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF990000),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                categoryToDelete = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { 
                Text("Apakah Anda yakin ingin menghapus kategori \"${categoryToDelete!!.title}\"? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoryToDelete?.let { category ->
                            viewModel.deleteCategory(category.id)
                        }
                        showDeleteDialog = false
                        categoryToDelete = null
                    }
                ) {
                    Text("OK", color = Color(0xFF990000))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        categoryToDelete = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleting: Boolean,
    position: Int = 0,
    totalItems: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = AccessibilityUtil.createListItemDescription(
                    itemName = "Kategori ${category.title}",
                    itemDetails = "Slug: ${category.slug}",
                    position = position + 1,
                    totalItems = totalItems,
                    hasActions = true
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Category Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics {
                        contentDescription = "Nama kategori: ${category.title}"
                    }
                )
                Text(
                    text = category.slug,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics {
                        contentDescription = "Slug kategori: ${category.slug}"
                    }
                )
            }
            
            // Action Buttons
            Row {
                IconButton(
                    onClick = onEditClick,
                    enabled = !isDeleting,
                    modifier = Modifier.semantics {
                        contentDescription = AccessibilityUtil.createButtonDescription(
                            buttonText = "Edit kategori ${category.title}",
                            isEnabled = !isDeleting,
                            isLoading = false,
                            additionalInfo = "Navigasi ke halaman edit kategori"
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null, // Handled by button semantics
                        tint = if (isDeleting) Color.Gray else Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    enabled = !isDeleting,
                    modifier = Modifier.semantics {
                        contentDescription = AccessibilityUtil.createButtonDescription(
                            buttonText = "Hapus kategori ${category.title}",
                            isEnabled = !isDeleting,
                            isLoading = isDeleting,
                            additionalInfo = if (isDeleting) "Sedang menghapus kategori" else "Tampilkan konfirmasi hapus"
                        )
                    }
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF990000),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null, // Handled by button semantics
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}