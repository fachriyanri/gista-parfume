package com.example.gistaparfume.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gistaparfume.data.entity.UserRole

@Composable
fun TopHeader(
    widthSizeClass: WindowWidthSizeClass,
    onRegisterClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    currentUser: com.example.gistaparfume.data.entity.UserEntity? = null,
    isLoggedIn: Boolean = false,
    onLogout: () -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    onCategoryManagementClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val isCompact = widthSizeClass == WindowWidthSizeClass.Compact

    if (isCompact) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF990000))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            TitleText(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavButtons(
                    onRegisterClick = onRegisterClick,
                    onHomeClick = onHomeClick,
                    onLoginClick = onLoginClick,
                    currentUser = currentUser,
                    isLoggedIn = isLoggedIn,
                    onLogout = onLogout,
                    onUserManagementClick = onUserManagementClick,
                    onCategoryManagementClick = onCategoryManagementClick,
                    onProfileClick = onProfileClick
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF990000))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TitleText()
            Row(verticalAlignment = Alignment.CenterVertically) {
                NavButtons(
                    onRegisterClick = onRegisterClick,
                    onHomeClick = onHomeClick,
                    onLoginClick = onLoginClick,
                    currentUser = currentUser,
                    isLoggedIn = isLoggedIn,
                    onLogout = onLogout,
                    onUserManagementClick = onUserManagementClick,
                    onCategoryManagementClick = onCategoryManagementClick,
                    onProfileClick = onProfileClick
                )
            }
        }
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier) {
    Text(
        "Gista Parfum",
        color = Color.White,
        fontSize = 14.sp,
        modifier = modifier
    )
}

@Composable
fun NavButtons(
    onRegisterClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    currentUser: com.example.gistaparfume.data.entity.UserEntity? = null,
    isLoggedIn: Boolean = false,
    onLogout: () -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    onCategoryManagementClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var showUserDropdown by remember { mutableStateOf(false) }
    var showManageDropdown by remember { mutableStateOf(false) }
    
    TextButton(onClick = onHomeClick) { 
        Text("Home", color = Color.White, fontSize = 12.sp) 
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
        }
        Text("Cart(0)", color = Color.White, fontSize = 12.sp)
    }

    if (isLoggedIn && currentUser != null) {
        // Admin-specific "Manage" dropdown with proper positioning
        if (currentUser.role == UserRole.ADMIN) {
            Box {
                TextButton(
                    onClick = { showManageDropdown = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Manage", color = Color.White, fontSize = 12.sp)
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Manage Menu",
                            tint = Color.White
                        )
                    }
                }
                
                DropdownMenu(
                    expanded = showManageDropdown,
                    onDismissRequest = { showManageDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("kategori") },
                        onClick = {
                            showManageDropdown = false
                            onCategoryManagementClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("produk") },
                        onClick = {
                            showManageDropdown = false
                            // TODO: Navigate to product management
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("order") },
                        onClick = {
                            showManageDropdown = false
                            // TODO: Navigate to order management
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("pengguna") },
                        onClick = {
                            showManageDropdown = false
                            onUserManagementClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("laporan penjualan") },
                        onClick = {
                            showManageDropdown = false
                            // TODO: Navigate to sales reports
                        }
                    )
                }
            }
        }
        Box {
            TextButton(
                onClick = { showUserDropdown = true }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(currentUser.name, color = Color.White, fontSize = 12.sp)
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "User Menu",
                        tint = Color.White
                    )
                }
            }

            DropdownMenu(
                expanded = showUserDropdown,
                onDismissRequest = { showUserDropdown = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = {
                        showUserDropdown = false
                        onProfileClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Orders") },
                    onClick = {
                        showUserDropdown = false
                        // TODO: Navigate to orders
                    }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        showUserDropdown = false
                        onLogout()
                    }
                )
            }
        }
    } else {
        // Not logged in - show Login and Register buttons
        TextButton(onClick = onLoginClick) { 
            Text("Login", color = Color.White, fontSize = 12.sp) 
        }
        TextButton(onClick = onRegisterClick) { 
            Text("Register", color = Color.White, fontSize = 12.sp) 
        }
    }
}


