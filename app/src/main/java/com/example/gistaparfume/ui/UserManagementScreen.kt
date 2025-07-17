package com.example.gistaparfume.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.viewmodel.UserManagementViewModel
import com.example.gistaparfume.utils.ImageUtils
import androidx.compose.runtime.LaunchedEffect


@Composable
fun UserManagementScreen(
    widthSizeClass: WindowWidthSizeClass,
    onHomeClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onAddUserClick: () -> Unit = {},
    onEditUserClick: (Int) -> Unit = {},
    currentUser: UserEntity? = null,
    isLoggedIn: Boolean = false,
    viewModel: UserManagementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Delete confirmation dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            // If the user is no longer logged in, go back to the home screen.
            onHomeClick()
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
            onUserManagementClick = {} // Already on user management screen
        )

        HorizontalDivider(color = Color.White, thickness = 7.dp)

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Page Title
            Text(
                text = "Pengguna",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF990000),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar and Add Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("cari") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Row {
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchText = ""
                                        viewModel.clearSearch()
                                        keyboardController?.hide()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Reset",
                                        tint = Color(0xFF990000)
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    viewModel.searchUsers(searchText)
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF990000)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchUsers(searchText)
                            keyboardController?.hide()
                        }
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Add User Button
                Button(
                    onClick = onAddUserClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF990000)
                    ),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add User",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Tambah",
                        color = Color.White,
                        fontSize = 14.sp
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

            // User List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF990000)
                    )
                }
            } else if (uiState.hasUsers) {
                // Mobile-friendly user list
                if (widthSizeClass == WindowWidthSizeClass.Compact) {
                    MobileUserList(
                        users = uiState.users,
                        onEditClick = onEditUserClick,
                        onDeleteClick = { userId ->
                            // Find the user to delete and show confirmation dialog
                            userToDelete = uiState.users.find { it.id == userId }
                            showDeleteDialog = true
                        },
                        getUserDisplayNumber = { index ->
                            viewModel.getUserDisplayNumber(index)
                        },
                        getRoleDisplayText = { user ->
                            viewModel.getRoleDisplayText(user)
                        },
                        getStatusDisplayText = { user ->
                            viewModel.getStatusDisplayText(user)
                        },
                        isDeleting = uiState.isDeleting
                    )
                } else {
                    // Desktop/tablet table view
                    UserTable(
                        users = uiState.users,
                        onEditClick = onEditUserClick,
                        onDeleteClick = { userId ->
                            viewModel.deleteUser(userId)
                        },
                        getUserDisplayNumber = { index ->
                            viewModel.getUserDisplayNumber(index)
                        },
                        getRoleDisplayText = { user ->
                            viewModel.getRoleDisplayText(user)
                        },
                        getStatusDisplayText = { user ->
                            viewModel.getStatusDisplayText(user)
                        },
                        isDeleting = uiState.isDeleting
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pagination Controls
                if (uiState.shouldShowPagination) {
                    PaginationControls(
                        currentPage = uiState.currentPage,
                        totalPages = uiState.totalPages,
                        onPreviousClick = { viewModel.previousPage() },
                        onNextClick = { viewModel.nextPage() },
                        onPageClick = { page -> viewModel.goToPage(page) },
                        hasPreviousPage = viewModel.hasPreviousPage(),
                        hasNextPage = viewModel.hasNextPage(),
                        paginationInfo = viewModel.getPaginationInfo()
                    )
                }
            } else {
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
                                "Tidak ada pengguna yang ditemukan untuk pencarian \"${uiState.searchQuery}\""
                            } else {
                                "Belum ada pengguna"
                            },
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.isSearchActive) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    searchText = ""
                                    viewModel.clearSearch()
                                }
                            ) {
                                Text(
                                    "Tampilkan semua pengguna",
                                    color = Color(0xFF990000)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                userToDelete = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { 
                Text("Apakah Anda yakin ingin menghapus pengguna \"${userToDelete!!.name}\"? Tindakan ini tidak dapat dibatalkan.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userToDelete?.let { user ->
                            viewModel.deleteUser(user.id)
                        }
                        showDeleteDialog = false
                        userToDelete = null
                    }
                ) {
                    Text("OK", color = Color(0xFF990000))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        userToDelete = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun UserTable(
    users: List<UserEntity>,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    getUserDisplayNumber: (Int) -> Int,
    getRoleDisplayText: (UserEntity) -> String,
    getStatusDisplayText: (UserEntity) -> String,
    isDeleting: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Pengguna",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Role",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Aksi",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(120.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            HorizontalDivider(color = Color(0xFFE0E0E0))
            
            // Table Rows
            LazyColumn {
                itemsIndexed(users) { index, user ->
                    UserTableRow(
                        user = user,
                        displayNumber = getUserDisplayNumber(index),
                        roleText = getRoleDisplayText(user),
                        statusText = getStatusDisplayText(user),
                        onEditClick = { onEditClick(user.id) },
                        onDeleteClick = { onDeleteClick(user.id) },
                        isDeleting = isDeleting
                    )
                    
                    if (index < users.size - 1) {
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                    }
                }
            }
        }
    }
}

@Composable
private fun UserTableRow(
    user: UserEntity,
    displayNumber: Int,
    roleText: String,
    statusText: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleting: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Number
        Text(
            text = displayNumber.toString(),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )
        
        // User (Image + Name)
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image - Show actual image or default avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF990000)),
                contentAlignment = Alignment.Center
            ) {
                if (user.image != null && ImageUtils.imageExists(user.image)) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ImageUtils.getImageUri(user.image))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Photo of ${user.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default avatar with user's initial
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = user.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        // Email
        Text(
            text = user.email,
            modifier = Modifier.weight(2f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Role
        Text(
            text = roleText,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = if (roleText == "admin") Color(0xFF990000) else Color(0xFF666666),
            fontWeight = if (roleText == "admin") FontWeight.Bold else FontWeight.Normal
        )
        
        // Status
        Text(
            text = statusText,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = if (statusText == "Aktif") Color(0xFF4CAF50) else Color(0xFF757575),
            fontWeight = FontWeight.Medium
        )
        
        // Actions
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onEditClick,
                enabled = !isDeleting
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit User",
                    tint = Color(0xFF2196F3)
                )
            }
            
            IconButton(
                onClick = onDeleteClick,
                enabled = !isDeleting
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
                        contentDescription = "Delete User",
                        tint = Color(0xFFE53935)
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileUserList(
    users: List<UserEntity>,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    getUserDisplayNumber: (Int) -> Int,
    getRoleDisplayText: (UserEntity) -> String,
    getStatusDisplayText: (UserEntity) -> String,
    isDeleting: Boolean
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(users) { index, user ->
            MobileUserCard(
                user = user,
                displayNumber = getUserDisplayNumber(index),
                roleText = getRoleDisplayText(user),
                statusText = getStatusDisplayText(user),
                onEditClick = { onEditClick(user.id) },
                onDeleteClick = { onDeleteClick(user.id) },
                isDeleting = isDeleting
            )
        }
    }
}

@Composable
private fun MobileUserCard(
    user: UserEntity,
    displayNumber: Int,
    roleText: String,
    statusText: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleting: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF990000)),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.image != null && ImageUtils.imageExists(user.image)) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(ImageUtils.getImageUri(user.image))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Photo of ${user.name}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // User Name and Email
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.email,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Action Buttons
                Row {
                    IconButton(
                        onClick = onEditClick,
                        enabled = !isDeleting
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit User",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDeleteClick,
                        enabled = !isDeleting
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
                                contentDescription = "Delete User",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Role and Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Role
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Role",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = roleText,
                        fontSize = 14.sp,
                        color = if (roleText == "admin") Color(0xFF990000) else Color(0xFF666666),
                        fontWeight = if (roleText == "admin") FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        fontSize = 14.sp,
                        color = if (statusText == "Aktif") Color(0xFF4CAF50) else Color(0xFF757575),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onPageClick: (Int) -> Unit,
    hasPreviousPage: Boolean,
    hasNextPage: Boolean,
    paginationInfo: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pagination Info
        Text(
            text = paginationInfo,
            color = Color(0xFF666666),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Pagination Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Previous Button
            TextButton(
                onClick = onPreviousClick,
                enabled = hasPreviousPage
            ) {
                Text(
                    "Sebelumnya",
                    color = if (hasPreviousPage) Color(0xFF990000) else Color(0xFFBDBDBD)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Page Numbers
            val startPage = maxOf(1, currentPage - 2)
            val endPage = minOf(totalPages, currentPage + 2)
            
            for (page in startPage..endPage) {
                if (page == currentPage) {
                    // Current page
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFF990000),
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = page.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Other pages
                    TextButton(
                        onClick = { onPageClick(page) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text(
                            text = page.toString(),
                            color = Color(0xFF990000)
                        )
                    }
                }
                
                if (page < endPage) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Next Button
            TextButton(
                onClick = onNextClick,
                enabled = hasNextPage
            ) {
                Text(
                    "Selanjutnya",
                    color = if (hasNextPage) Color(0xFF990000) else Color(0xFFBDBDBD)
                )
            }
        }
    }
}