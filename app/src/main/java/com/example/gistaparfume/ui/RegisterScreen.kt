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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.gistaparfume.data.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    widthSizeClass: WindowWidthSizeClass,
    viewModel: RegisterViewModel,
    onNavigateToHome: () -> Unit = {},
    onShowToast: (String) -> Unit = {},
    onNavigateToHomeFromHeader: () -> Unit = {}
) {
    // Set callbacks in viewModel
    LaunchedEffect(Unit) {
        viewModel.setNavigationCallback(onNavigateToHome)
        viewModel.setToastCallback(onShowToast)
    }
    
    // Handle registration success - removed immediate handling to allow dialog to show
    
    // Handle success and error dialogs
    RegistrationDialogs(viewModel = viewModel)
    
    when (widthSizeClass) {
        WindowWidthSizeClass.Expanded -> {
            // ===== TABLET / DESKTOP =====
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TopHeader(widthSizeClass, onRegisterClick = {}, onHomeClick = onNavigateToHomeFromHeader)
                    HorizontalDivider(color = Color.White, thickness = 7.dp)
                    
                    // Main content area with centered form
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        RegisterForm(
                            viewModel = viewModel,
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
                TopHeader(widthSizeClass, onRegisterClick = {}, onHomeClick = onNavigateToHomeFromHeader)
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with optimized spacing
                RegisterForm(
                    viewModel = viewModel,
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
                TopHeader(widthSizeClass, onRegisterClick = {}, onHomeClick = onNavigateToHomeFromHeader)
                HorizontalDivider(color = Color.White, thickness = 7.dp)
                
                // Form with mobile spacing
                RegisterForm(
                    viewModel = viewModel,
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
private fun RegisterForm(
    viewModel: RegisterViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Form Title
        Text(
            text = "Formulir Registrasi",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Name Input Field
        NameInputField(
            value = viewModel.formState.name,
            onValueChange = viewModel::updateName,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Email Input Field with validation
        EmailInputField(
            value = viewModel.formState.email,
            onValueChange = viewModel::updateEmail,
            isError = viewModel.formState.emailError != null,
            errorMessage = viewModel.formState.emailError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Password Input Field with validation
        PasswordInputField(
            value = viewModel.formState.password,
            onValueChange = viewModel::updatePassword,
            isError = viewModel.formState.passwordError != null,
            errorMessage = viewModel.formState.passwordError,
            placeholder = "Masukan password minimal 8 character",
            modifier = Modifier.fillMaxWidth()
        )
        
        // Confirm Password Input Field with validation
        PasswordInputField(
            value = viewModel.formState.confirmPassword,
            onValueChange = viewModel::updateConfirmPassword,
            isError = viewModel.formState.confirmPasswordError != null,
            errorMessage = viewModel.formState.confirmPasswordError,
            placeholder = "Masukan password yang sama",
            label = "Konfirmasi Password",
            modifier = Modifier.fillMaxWidth()
        )
        
        // Error message display
        ErrorMessageDisplay(
            errorMessage = viewModel.errorMessage,
            onDismiss = viewModel::clearError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Sign Up Button
        SignUpButton(
            isEnabled = viewModel.formState.isFormValid,
            isLoading = viewModel.isLoading,
            onClick = viewModel::submitRegistration,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Nama") },
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
    )
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
    isError: Boolean,
    errorMessage: String?,
    placeholder: String,
    label: String = "Password",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            isError = isError,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
private fun SignUpButton(
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
                    text = "Mendaftar...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RegistrationDialogs(viewModel: RegisterViewModel) {
    // Success Dialog
    if (viewModel.isRegistrationSuccess) {
        SuccessDialog(
            viewModel = viewModel,
            onDismiss = {
                viewModel.resetRegistrationSuccess()
            }
        )
    }
}

@Composable
private fun SuccessDialog(
    viewModel: RegisterViewModel,
    onDismiss: () -> Unit
) {
    var hasHandledSuccess by remember { mutableStateOf(false) }
    
    // Auto-dismiss after 5 seconds and handle success actions
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000L) // 5 seconds delay
        if (!hasHandledSuccess) {
            hasHandledSuccess = true
            viewModel.handleRegistrationSuccess() // Handle toast and navigation after delay
            onDismiss()
        }
    }
    
    AlertDialog(
        onDismissRequest = {
            // If user manually dismisses, still handle success actions
            if (!hasHandledSuccess) {
                hasHandledSuccess = true
                viewModel.handleRegistrationSuccess()
            }
            onDismiss()
        },
        title = {
            Text(
                text = "Registrasi Berhasil!",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Text(
                text = "Akun Anda telah berhasil dibuat. Selamat datang di aplikasi kami!",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Handle success actions when OK is clicked
                    if (!hasHandledSuccess) {
                        hasHandledSuccess = true
                        viewModel.handleRegistrationSuccess()
                    }
                    onDismiss()
                }
            ) {
                Text(
                    text = "OK",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface
    )
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