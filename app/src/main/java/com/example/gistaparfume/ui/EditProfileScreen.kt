package com.example.gistaparfume.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gistaparfume.R
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.viewmodel.ProfileViewModel

/**
 * EditProfileScreen composable that provides form for editing user profile information
 * Includes circular profile picture, form fields for name/email/password, and save functionality
 * Integrates with TopHeader for navigation
 */
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel,
    currentUser: UserEntity?,
    widthSizeClass: WindowWidthSizeClass,
    onHomeClick: () -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onProfileClick: () -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    isLoggedIn: Boolean
) {
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onHomeClick()
        }
    }

    val formState by profileViewModel.formState.collectAsState()
    
    // Load user profile when screen is first displayed
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            profileViewModel.loadUserProfile(userId)
        }
    }
    
    // Handle successful update - navigate back to profile screen
    LaunchedEffect(formState.isUpdateSuccessful) {
        if (formState.isUpdateSuccessful) {
            profileViewModel.clearUpdateSuccess()
            onNavigateBack()
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
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            EditProfileContent(
                formState = formState,
                profileViewModel = profileViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Main content for the edit profile screen
 */
@Composable
private fun EditProfileContent(
    formState: com.example.gistaparfume.data.viewmodel.ProfileFormState,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Edit Profil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Profile Picture Section
        EditableProfileImage(
            imageUri = formState.imageUri,
            imageError = formState.imageError,
            onImageSelected = { uri ->
                profileViewModel.updateFormImage(uri)
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Form Fields
        EditProfileForm(
            formState = formState,
            profileViewModel = profileViewModel,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        ErrorMessageDisplay(
            errorMessage = formState.errorMessage,
            onDismiss = profileViewModel::clearErrors,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Save Button
        SaveChangesButton(
            isEnabled = formState.isFormValid && !formState.isLoading,
            isLoading = formState.isLoading,
            onClick = profileViewModel::saveProfileChanges,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )
    }
}

/**
 * Editable profile image with "Ubah Foto" functionality
 */
@Composable
private fun EditableProfileImage(
    imageUri: String?,
    imageError: String?,
    onImageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Circular profile picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable {
                    imagePickerLauncher.launch("image/*")
                }
        ) {
            ProfileImage(
                imageUri = imageUri,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // "Ubah Foto" button
        TextButton(
            onClick = {
                imagePickerLauncher.launch("image/*")
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Memberi jarak otomatis
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Ubah Foto",
                    tint = Color(0xFF990000) // Samakan warna dengan teks
                )
                Text(
                    text = "Ubah Foto",
                    color = Color(0xFF990000),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Image error message display
        if (imageError != null) {
            Text(
                text = imageError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
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
            fallback = painterResource(id = R.drawable.ic_default_profile),
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

/**
 * Form fields for editing profile information
 */
@Composable
private fun EditProfileForm(
    formState: com.example.gistaparfume.data.viewmodel.ProfileFormState,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name Input Field
        NameInputField(
            value = formState.name,
            onValueChange = profileViewModel::updateFormName,
            isError = formState.nameError != null,
            errorMessage = formState.nameError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Email Input Field
        EmailInputField(
            value = formState.email,
            onValueChange = profileViewModel::updateFormEmail,
            isError = formState.emailError != null,
            errorMessage = formState.emailError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password Input Field
        PasswordInputField(
            value = formState.password,
            onValueChange = profileViewModel::updateFormPassword,
            isPasswordVisible = formState.isPasswordVisible,
            onTogglePasswordVisibility = profileViewModel::togglePasswordVisibility,
            isError = formState.passwordError != null,
            errorMessage = formState.passwordError,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Name input field with validation
 */
@Composable
private fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Nama") },
            placeholder = { Text("Masukan nama lengkap") },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Email input field with validation
 */
@Composable
private fun EmailInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("E-mail") },
            placeholder = { Text("Masukan alamat email") },
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Password input field with visibility toggle and validation
 */
@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Password") },
            placeholder = { Text("Masukan password baru (opsional)") },
            singleLine = true,
            isError = isError,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (isPasswordVisible) {
                            "Sembunyikan password"
                        } else {
                            "Tampilkan password"
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Save changes button with loading state
 */
@Composable
private fun SaveChangesButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF990000)
        ),
        modifier = modifier.height(48.dp)
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(16.dp).height(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Text(
                    text = "Menyimpan...",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = "Simpan Perubahan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Error message display component
 */
@Composable
private fun ErrorMessageDisplay(
    errorMessage: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (errorMessage != null) {
        Card(
            modifier = modifier.padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Tutup",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}