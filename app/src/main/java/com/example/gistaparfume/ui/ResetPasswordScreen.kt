package com.example.gistaparfume.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gistaparfume.data.viewmodel.LoginViewModel

@Composable
fun ResetPasswordScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: LoginViewModel,
    email: String, // Email passed from forgot password screen
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHomeFromHeader: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                        onHomeClick = onNavigateToHomeFromHeader,
                        onLoginClick = onNavigateToLogin,
                        onRegisterClick = onNavigateToRegister
                    )
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    
                    // Main content area with centered form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ResetPasswordForm(
                            viewModel = viewModel,
                            uiState = uiState,
                            email = email,
                            onNavigateToLogin = onNavigateToLogin,
                            onNavigateToHome = onNavigateToHome,
                            modifier = Modifier
                                .widthIn(max = 600.dp)
                                .padding(horizontal = 32.dp, vertical = 24.dp)
                        )
                    }
                    
                    Footer(widthSizeClass, onHomeClick = onNavigateToHomeFromHeader)
                }
            }
        }
        WindowWidthSizeClass.Medium -> {
            // ===== MEDIUM PHONE =====
            Column(modifier = Modifier.fillMaxSize()) {
                TopHeader(
                    widthSizeClass = widthSizeClass,
                    onHomeClick = onNavigateToHomeFromHeader,
                    onLoginClick = onNavigateToLogin,
                    onRegisterClick = onNavigateToRegister
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with optimized spacing
                ResetPasswordForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    email = email,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToHome = onNavigateToHome,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
                
                Footer(widthSizeClass, onHomeClick = onNavigateToHomeFromHeader)
            }
        }
        else -> {
            // ===== COMPACT / MOBILE =====
            Column(modifier = Modifier.fillMaxSize()) {
                TopHeader(
                    widthSizeClass = widthSizeClass,
                    onHomeClick = onNavigateToHomeFromHeader,
                    onLoginClick = onNavigateToLogin,
                    onRegisterClick = onNavigateToRegister
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with mobile spacing
                ResetPasswordForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    email = email,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToHome = onNavigateToHome,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
                
                Footer(widthSizeClass, onHomeClick = onNavigateToHomeFromHeader)
            }
        }
    }
}

@Composable
private fun ResetPasswordForm(
    viewModel: LoginViewModel,
    uiState: com.example.gistaparfume.data.LoginUiState,
    email: String,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var resetCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Instructions
        Text(
            text = "Masukkan kode reset yang dikirim ke email Anda dan password baru",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Email display (read-only)
        OutlinedTextField(
            value = email,
            onValueChange = { },
            label = { Text("Email") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Reset Code Input
        OutlinedTextField(
            value = resetCode,
            onValueChange = { resetCode = it },
            label = { Text("Kode Reset") },
            placeholder = { Text("Masukkan kode reset dari email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        // New Password Input
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Password Baru") },
            placeholder = { Text("Masukkan password baru") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Confirm Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Password") },
            placeholder = { Text("Konfirmasi password baru") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password mismatch error
        if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
            Text(
                text = "Password tidak cocok",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Success message display
        if (successMessage != null) {
            SuccessMessageDisplay(
                message = successMessage!!,
                onDismiss = { 
                    successMessage = null
                    onNavigateToHome() // Navigate to homepage when "Tutup" is clicked
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Error message display
        if (errorMessage != null) {
            ErrorMessageDisplay(
                errorMessage = errorMessage!!,
                onDismiss = { errorMessage = null },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Reset Password Button
        Button(
            onClick = {
                // Validate inputs
                when {
                    resetCode.trim().isEmpty() -> {
                        errorMessage = "Kode reset tidak boleh kosong"
                    }
                    newPassword.isEmpty() -> {
                        errorMessage = "Password baru tidak boleh kosong"
                    }
                    newPassword.length < 8 -> {
                        errorMessage = "Password minimal 8 karakter"
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "Konfirmasi password tidak cocok"
                    }
                    else -> {
                        // Perform password reset
                        isLoading = true
                        errorMessage = null
                        viewModel.resetPasswordWithToken(
                            email = email,
                            resetToken = resetCode.trim(),
                            newPassword = newPassword,
                            onSuccess = {
                                isLoading = false
                                successMessage = "Password berhasil direset! Silakan login dengan password baru."
                                // Clear form
                                resetCode = ""
                                newPassword = ""
                                confirmPassword = ""
                            },
                            onError = { error ->
                                isLoading = false
                                errorMessage = error
                            }
                        )
                    }
                }
            },
            enabled = resetCode.trim().isNotEmpty() && 
                     newPassword.isNotEmpty() && 
                     confirmPassword.isNotEmpty() && 
                     newPassword == confirmPassword && 
                     !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 8.dp)
        ) {
            if (isLoading) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(16.dp).height(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Mereset...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = "Reset Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Back to Login Link
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Kembali ke Login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorMessageDisplay(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
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

@Composable
private fun SuccessMessageDisplay(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E8) // Light green background
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
                text = message,
                color = Color(0xFF2E7D32), // Dark green text
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Tutup",
                    color = Color(0xFF2E7D32), // Dark green text
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

