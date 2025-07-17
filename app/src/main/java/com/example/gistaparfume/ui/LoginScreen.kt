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
fun LoginScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: LoginViewModel,
    onNavigateToHome: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToHomeFromHeader: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle successful login navigation
    if (uiState.isLoggedIn) {
        onNavigateToHome()
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
                        onRegisterClick = onNavigateToRegister,
                        onHomeClick = onNavigateToHomeFromHeader,
                        onLoginClick = {}
                    )
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    
                    // Main content area with centered form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoginForm(
                            viewModel = viewModel,
                            uiState = uiState,
                            onNavigateToRegister = onNavigateToRegister,
                            onNavigateToForgotPassword = onNavigateToForgotPassword,
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
                    onRegisterClick = onNavigateToRegister,
                    onHomeClick = onNavigateToHomeFromHeader,
                    onLoginClick = {}
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with optimized spacing
                LoginForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToRegister = onNavigateToRegister,
                    onNavigateToForgotPassword = onNavigateToForgotPassword,
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
                    onRegisterClick = onNavigateToRegister,
                    onHomeClick = onNavigateToHomeFromHeader,
                    onLoginClick = {}
                )
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with mobile spacing
                LoginForm(
                    viewModel = viewModel,
                    uiState = uiState,
                    onNavigateToRegister = onNavigateToRegister,
                    onNavigateToForgotPassword = onNavigateToForgotPassword,
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
private fun LoginForm(
    viewModel: LoginViewModel,
    uiState: com.example.gistaparfume.data.LoginUiState,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Formulir Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Email Input Field
        EmailInputField(
            value = viewModel.email,
            onValueChange = viewModel::updateEmail,
            isError = !viewModel.isEmailValid,
            errorMessage = viewModel.emailError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password Input Field
        PasswordInputField(
            value = viewModel.password,
            onValueChange = viewModel::updatePassword,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        ErrorMessageDisplay(
            errorMessage = uiState.errorMessage,
            onDismiss = viewModel::clearError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Login Button
        LoginButton(
            isEnabled = viewModel.email.isNotEmpty() && viewModel.password.isNotEmpty() && viewModel.isEmailValid,
            isLoading = uiState.isLoading,
            onClick = viewModel::login,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        
        // Navigation Links
        NavigationLinks(
            onNavigateToForgotPassword = onNavigateToForgotPassword,
            onNavigateToRegister = onNavigateToRegister,
            modifier = Modifier.padding(top = 16.dp)
        )
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

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        placeholder = { Text("Masukan password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier
    )
}

@Composable
private fun LoginButton(
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
                    text = "Masuk...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NavigationLinks(
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(onClick = onNavigateToForgotPassword) {
            Text(
                text = "Forgot Password?",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
        
        TextButton(onClick = onNavigateToRegister) {
            Text(
                text = "Create an Account",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}

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