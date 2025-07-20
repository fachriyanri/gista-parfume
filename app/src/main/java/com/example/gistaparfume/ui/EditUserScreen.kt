package com.example.gistaparfume.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gistaparfume.data.UserFormState
import com.example.gistaparfume.data.entity.UserEntity
import com.example.gistaparfume.data.entity.UserRole
import com.example.gistaparfume.utils.ImageUtils

@Composable
fun EditUserScreen(
    widthSizeClass: WindowWidthSizeClass,
    userId: Int,
    formState: UserFormState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onStatusChange: (Boolean) -> Unit,
    onImageSelected: (String?) -> Unit,
    onUpdateUser: () -> Unit,
    onLoadUser: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit,
    currentUser: UserEntity? = null,
    isLoggedIn: Boolean = false,
    onProfileClick: () -> Unit,
) {
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            onNavigateToHome() // Gunakan fungsi navigasi ke home
        }
    }

    // Load user data when screen is first displayed
    LaunchedEffect(userId) {
        onLoadUser(userId)
    }


    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }


    // Error dialog
    if (formState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { /* Handle error dismissal if needed */ },
            title = { Text("Error") },
            text = { Text(formState.errorMessage) },
            confirmButton = {
                TextButton(onClick = { /* Handle error dismissal if needed */ }) {
                    Text("OK")
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
                        onUserManagementClick = {}, // Part of user management flow
                        onProfileClick = onProfileClick
                    )
                    HorizontalDivider(color = Color.White, thickness = 7.dp)

                    // Main content area with centered form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EditUserForm(
                            formState = formState,
                            onNameChange = onNameChange,
                            onEmailChange = onEmailChange,
                            onPasswordChange = onPasswordChange,
                            onRoleChange = onRoleChange,
                            onStatusChange = onStatusChange,
                            onImageClick = { imagePickerLauncher.launch("image/*") },
                            onUpdateUser = onUpdateUser,
                            modifier = Modifier
                                .widthIn(max = 600.dp)
                                .padding(horizontal = 32.dp, vertical = 24.dp)
                        )
                    }

                    Footer(widthSizeClass, onHomeClick = onNavigateToHome)
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
                    onUserManagementClick = {}, // Part of user management flow,
                    onProfileClick = onProfileClick
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)

                // Form with optimized spacing
                EditUserForm(
                    formState = formState,
                    onNameChange = onNameChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onRoleChange = onRoleChange,
                    onStatusChange = onStatusChange,
                    onImageClick = { imagePickerLauncher.launch("image/*") },
                    onUpdateUser = onUpdateUser,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )

                Footer(widthSizeClass, onHomeClick = onNavigateToHome)
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
                    onUserManagementClick = {}, // Part of user management flow
                    onProfileClick = onProfileClick
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)

                // Form with mobile spacing
                EditUserForm(
                    formState = formState,
                    onNameChange = onNameChange,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onRoleChange = onRoleChange,
                    onStatusChange = onStatusChange,
                    onImageClick = { imagePickerLauncher.launch("image/*") },
                    onUpdateUser = onUpdateUser,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )

                Footer(widthSizeClass, onHomeClick = onNavigateToHome)
            }
        }
    }
}

@Composable
private fun EditUserForm(
    formState: UserFormState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onStatusChange: (Boolean) -> Unit,
    onImageClick: () -> Unit,
    onUpdateUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Formulir Pengguna",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Show loading indicator while loading user data
        if (formState.isLoading && !formState.isEditMode) {
            CircularProgressIndicator(
                modifier = Modifier.padding(32.dp),
                color = Color(0xFF990000)
            )
        } else {
            // Profile Photo Section with current image display
            EditProfilePhotoSection(
                currentImageUri = formState.imageUri,
                onImageClick = onImageClick,
                isError = formState.imageError != null,
                errorMessage = formState.imageError,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Name Input Field
            NameInputField(
                value = formState.name,
                onValueChange = onNameChange,
                isError = formState.nameError != null,
                errorMessage = formState.nameError,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Email Input Field with validation
            EmailInputField(
                value = formState.email,
                onValueChange = onEmailChange,
                isError = formState.emailError != null,
                errorMessage = formState.emailError,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Password Input Field with validation (optional for edit)
            PasswordInputField(
                value = formState.password,
                onValueChange = onPasswordChange,
                isError = formState.passwordError != null,
                errorMessage = formState.passwordError,
                isEditMode = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Role Selection
            RoleSelectionSection(
                selectedRole = formState.role,
                onRoleChange = onRoleChange,
                isError = formState.roleError != null,
                errorMessage = formState.roleError,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Status Selection
            StatusSelectionSection(
                isActive = formState.isActive,
                onStatusChange = onStatusChange,
                isError = formState.statusError != null,
                errorMessage = formState.statusError,
                modifier = Modifier.fillMaxWidth()
            )
            
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
            
            // Update Button
            UpdateButton(
                isEnabled = formState.isFormValid && !formState.isLoading,
                isLoading = formState.isLoading,
                onClick = onUpdateUser,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun EditProfilePhotoSection(
    currentImageUri: String?,
    onImageClick: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Foto",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Current image display
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
                .border(
                    2.dp, 
                    if (isError) Color.Red.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0.5f), 
                    CircleShape
                )
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            if (currentImageUri != null) {
                val imageModel = if (currentImageUri.startsWith("content://")) {
                    // Content URI from gallery picker
                    currentImageUri
                } else {
                    // File path from internal storage
                    ImageUtils.getImageUri(currentImageUri)
                }
                
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageModel)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Current Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Select Photo",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Gray
                )
            }
        }
        
        OutlinedButton(
            onClick = onImageClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ubah Foto")
        }
        
        // Error message display
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

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
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

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
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(if (isEditMode) "Password (kosongkan jika tidak ingin mengubah)" else "Password") },
            singleLine = true,
            isError = isError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun RoleSelectionSection(
    selectedRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Role",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isError) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Admin option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedRole == UserRole.ADMIN,
                            onClick = { onRoleChange(UserRole.ADMIN) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedRole == UserRole.ADMIN,
                        onClick = { onRoleChange(UserRole.ADMIN) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin")
                }
                
                // Member option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedRole == UserRole.MEMBER,
                            onClick = { onRoleChange(UserRole.MEMBER) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedRole == UserRole.MEMBER,
                        onClick = { onRoleChange(UserRole.MEMBER) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Member")
                }
            }
        }
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun StatusSelectionSection(
    isActive: Boolean,
    onStatusChange: (Boolean) -> Unit,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Status",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isError) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Active option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = isActive,
                            onClick = { onStatusChange(true) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isActive,
                        onClick = { onStatusChange(true) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aktif")
                }
                
                // Inactive option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = !isActive,
                            onClick = { onStatusChange(false) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isActive,
                        onClick = { onStatusChange(false) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tidak Aktif")
                }
            }
        }
        
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun UpdateButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = modifier.height(48.dp),
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