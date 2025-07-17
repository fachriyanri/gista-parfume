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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gistaparfume.data.viewmodel.LoginViewModel

@Composable
fun ForgotPasswordScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: LoginViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHomeFromHeader: () -> Unit = {},
    onNavigateToResetPassword: (String) -> Unit = {},
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
                        ForgotPasswordForm(
                            viewModel = viewModel,
                            uiState = uiState,
                            onNavigateToLogin = onNavigateToLogin,
                            onNavigateToResetPassword = onNavigateToResetPassword,
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
                ForgotPasswordForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToResetPassword = onNavigateToResetPassword,
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
                ForgotPasswordForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToResetPassword = onNavigateToResetPassword,
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
private fun ForgotPasswordForm(
    viewModel: LoginViewModel,
    uiState: com.example.gistaparfume.data.LoginUiState,
    onNavigateToLogin: () -> Unit,
    onNavigateToResetPassword: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Store the email locally to use for navigation after it's cleared from viewModel
    var lastSentEmail by remember { mutableStateOf("") }
    
    // Update lastSentEmail when reset email changes and is not empty
    if (viewModel.resetEmail.isNotEmpty()) {
        lastSentEmail = viewModel.resetEmail
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Formulir Lupa Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Show success message if password reset email was sent
        if (uiState.isPasswordResetSent) {
            SuccessMessageDisplay(
                message = "Email reset password telah dikirim. Silakan periksa kotak masuk Anda.",
                onDismiss = viewModel::resetPasswordResetSentState,
                onContinue = { onNavigateToResetPassword(lastSentEmail) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Email Input Field
        ResetEmailInputField(
            value = viewModel.resetEmail,
            onValueChange = viewModel::updateResetEmail,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        PasswordResetErrorMessageDisplay(
            errorMessage = uiState.passwordResetErrorMessage,
            onDismiss = viewModel::clearPasswordResetError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Reset Password Button
        ResetPasswordButton(
            isEnabled = viewModel.resetEmail.isNotEmpty(),
            isLoading = uiState.isPasswordResetLoading,
            onClick = { viewModel.resetPassword() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        
        // Back to Login Link
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "back to login",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ResetEmailInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("E-mail") },
        placeholder = { Text("Masukan alamat email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = modifier
    )
}

@Composable
private fun ResetPasswordButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
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
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Mengirim...",
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
}



@Composable
private fun PasswordResetErrorMessageDisplay(
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

@Composable
private fun SuccessMessageDisplay(
    message: String,
    onDismiss: () -> Unit,
    onContinue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E8) // Light green background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = Color(0xFF2E7D32), // Dark green text
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Tutup",
                        color = Color(0xFF2E7D32), // Dark green text
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Button(
                    onClick = onContinue
                ) {
                    Text(
                        text = "Lanjutkan",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}