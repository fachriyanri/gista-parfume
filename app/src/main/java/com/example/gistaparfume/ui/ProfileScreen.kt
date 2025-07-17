package com.example.gistaparfume.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gistaparfume.R
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.viewmodel.ProfileViewModel

/**
 * ProfileScreen composable that displays user profile information
 * Shows circular profile picture, name, email, and edit button
 * Integrates with TopHeader for navigation
 */
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    currentUser: UserEntity?,
    widthSizeClass: WindowWidthSizeClass,
    onHomeClick: () -> Unit,
    onLogout: () -> Unit,
    onEditProfileClick: () -> Unit,
    onUserManagementClick: () -> Unit = {},
    isLoggedIn: Boolean
) {
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onHomeClick()
        }
    }

    val profileUiState by profileViewModel.profileUiState.collectAsState()
    
    // Load user profile when screen is first displayed
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            profileViewModel.loadUserProfile(userId)
        }
    }
    
    Scaffold(
        topBar = {
            TopHeader(
                widthSizeClass = widthSizeClass,
                onHomeClick = onHomeClick,
                currentUser = currentUser,
                isLoggedIn = isLoggedIn,
                onLogout = onLogout,
                onUserManagementClick = onUserManagementClick,
                onProfileClick = { /* Already on profile screen */ }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                profileUiState.isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF990000)
                        )
                    }
                }
                
                profileUiState.errorMessage != null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = profileUiState.errorMessage ?: "Terjadi kesalahan",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = {
                                    currentUser?.id?.let { userId ->
                                        profileViewModel.loadUserProfile(userId)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF990000)
                                )
                            ) {
                                Text("Coba Lagi", color = Color.White)
                            }
                        }
                    }
                }
                
                profileUiState.user != null -> {
                    // Profile content
                    profileUiState.user?.let { user ->
                        ProfileContent(
                            user = user,
                            onEditClick = onEditProfileClick
                        )
                    }
                }
                
                else -> {
                    // Fallback to current user if profile state is empty
                    currentUser?.let { user ->
                        ProfileContent(
                            user = user,
                            onEditClick = onEditProfileClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * Profile content composable that displays user information
 */
@Composable
private fun ProfileContent(
    user: UserEntity,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Circular profile picture
        ProfileImage(
            imageUri = user.image,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        
        // User name
        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        // User email
        Text(
            text = user.email,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        // Edit button
        Button(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF990000)
            )
        ) {
            Text(
                text = "Edit",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Profile image composable that handles both custom images and default placeholder
 */
@Composable
private fun ProfileImage(
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    if (!imageUri.isNullOrBlank()) {
        // Display user's profile image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Picture",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            fallback = painterResource(id = R.drawable.ic_default_profile), // Fallback to default profile if image fails to load
            error = painterResource(id = R.drawable.ic_default_profile)
        )
    } else {
        // Display default placeholder image
        Image(
            painter = painterResource(id = R.drawable.ic_default_profile),
            contentDescription = "Default Profile Picture",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}