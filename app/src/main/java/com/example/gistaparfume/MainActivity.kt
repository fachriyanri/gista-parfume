package com.example.gistaparfume
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gistaparfume.data.viewmodel.AddCategoryViewModel
import com.example.gistaparfume.data.viewmodel.CategoryManagementViewModel
import com.example.gistaparfume.data.viewmodel.CategoryViewModel
import com.example.gistaparfume.data.viewmodel.EditCategoryViewModel
import com.example.gistaparfume.data.viewmodel.LoginViewModel
import com.example.gistaparfume.data.viewmodel.ProductViewModel
import com.example.gistaparfume.data.viewmodel.ProfileViewModel
import com.example.gistaparfume.data.viewmodel.RegisterViewModel
import com.example.gistaparfume.data.viewmodel.UserManagementViewModel
import com.example.gistaparfume.navigation.NavigationRoutes
import com.example.gistaparfume.ui.AddCategoryScreen
import com.example.gistaparfume.ui.AddUserScreen
import com.example.gistaparfume.ui.CategoryManagementScreen
import com.example.gistaparfume.ui.EditCategoryScreen
import com.example.gistaparfume.ui.EditProfileScreen
import com.example.gistaparfume.ui.EditUserScreen
import com.example.gistaparfume.ui.ForgotPasswordScreen
import com.example.gistaparfume.ui.HomeScreen
import com.example.gistaparfume.ui.LoginScreen
import com.example.gistaparfume.ui.ProfileScreen
import com.example.gistaparfume.ui.RegisterScreen
import com.example.gistaparfume.ui.ResetPasswordScreen
import com.example.gistaparfume.ui.UserManagementScreen
import com.example.gistaparfume.ui.theme.GistaParfumeTheme
import com.example.gistaparfume.utils.CustomToast

class MainActivity : ComponentActivity() {
    private val productViewModel: ProductViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val categoryManagementViewModel: CategoryManagementViewModel by viewModels()
    private val addCategoryViewModel: AddCategoryViewModel by viewModels()
    private val editCategoryViewModel: EditCategoryViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val userManagementViewModel: UserManagementViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GistaParfumeTheme {
                val navController = rememberNavController()
                val windowSizeClass = calculateWindowSizeClass(this)
                
                val isAnyFilterActive by productViewModel.isAnyFilterActive.collectAsState()
                val selectedCategory by productViewModel.selectedCategory.collectAsState()
                val products by productViewModel.products.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()
                val isLoading = productViewModel.isLoading
                val sortDescending by productViewModel.sortDescending.collectAsState()
                
                // Authentication state
                val loginUiState by loginViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    productViewModel.setupDatabase()
                    productViewModel.initialize()
                    
                    // Check authentication state on app startup
                    loginViewModel.checkAuthenticationState()
                }
                
                // Handle navigation based on authentication state changes
                LaunchedEffect(loginUiState.isLoggedIn) {
                    // When user successfully logs in, navigate to home if currently on login screen
                    if (loginUiState.isLoggedIn && navController.currentDestination?.route == NavigationRoutes.LOGIN) {
                        navController.navigate(NavigationRoutes.HOME, NavOptions.Builder()
                            .setPopUpTo(NavigationRoutes.HOME, inclusive = false)
                            .build())
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.HOME
                ) {
                    composable(NavigationRoutes.HOME) {
                        HomeScreen(
                            products = products,
                            isLoading = isLoading,
                            onLoadMore = { productViewModel.loadNextPage() },
                            categories = categories.map { it.title },
                            selectedCategory = selectedCategory,
                            onAddToCart = { product, qty ->
                                // implement kalau perlu
                            },
                            onCategorySelected = { category ->
                                // When a category is picked, call onFilterChanged
                                productViewModel.onFilterChanged(category = category)
                            },
                            onSearch = { query ->
                                // When user searches, call onFilterChanged
                                productViewModel.onFilterChanged(query = query)
                            },
                            onSortByPrice = { isDescending  ->
                                productViewModel.onSortChanged(isDescending)
                            },
                            sortDescending = sortDescending,
                            onResetFilters = { productViewModel.resetFilters() },
                            isAnyFilterActive = isAnyFilterActive,
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            onRegisterClick = {
                                navController.navigate(NavigationRoutes.REGISTER)
                            },
                            onLoginClick = {
                                navController.navigate(NavigationRoutes.LOGIN)
                            },
                            // Pass authentication state to HomeScreen
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            },
                            onCategoryManagementClick = {
                                navController.navigate(NavigationRoutes.CATEGORY_MANAGEMENT)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.CATEGORY_MANAGEMENT) {
                        // Clear any form state when entering category management screen
                        LaunchedEffect(Unit) {
                            categoryManagementViewModel.clearError()
                        }
                        
                        CategoryManagementScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = categoryManagementViewModel,
                            onHomeClick = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onAddCategoryClick = {
                                navController.navigate(NavigationRoutes.ADD_CATEGORY)
                            },
                            onEditCategoryClick = { categoryId ->
                                navController.navigate(NavigationRoutes.createEditCategoryRoute(categoryId))
                            },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.ADD_CATEGORY) {
                        // Reset form when entering add category screen
                        LaunchedEffect(Unit) {
                            addCategoryViewModel.resetForm()
                            addCategoryViewModel.clearOperationSuccess()
                        }
                        
                        AddCategoryScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = addCategoryViewModel,
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onNavigateToCategoryManagement = {
                                navController.navigate(NavigationRoutes.CATEGORY_MANAGEMENT, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.CATEGORY_MANAGEMENT, inclusive = false)
                                    .build())
                            },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.EDIT_CATEGORY) { backStackEntry ->
                        val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull() ?: 0
                        
                        // Clear form state when entering edit category screen
                        LaunchedEffect(categoryId) {
                            if (categoryId > 0) {
                                editCategoryViewModel.clearOperationSuccess()
                            }
                        }
                        
                        EditCategoryScreen(
                            categoryId = categoryId,
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = editCategoryViewModel,
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onNavigateToCategoryManagement = {
                                navController.navigate(NavigationRoutes.CATEGORY_MANAGEMENT, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.CATEGORY_MANAGEMENT, inclusive = false)
                                    .build())
                            },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.LOGIN) {
                        LoginScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = loginViewModel,
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.HOME, inclusive = false)
                                    .build())
                            },
                            onNavigateToRegister = {
                                navController.navigate(NavigationRoutes.REGISTER)
                            },
                            onNavigateToForgotPassword = {
                                navController.navigate(NavigationRoutes.FORGOT_PASSWORD)
                            },
                            onNavigateToHomeFromHeader = {
                                navController.navigate(NavigationRoutes.HOME)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.FORGOT_PASSWORD) {
                        ForgotPasswordScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = loginViewModel,
                            onNavigateToLogin = {
                                navController.navigate(NavigationRoutes.LOGIN)
                            },
                            onNavigateToHomeFromHeader = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onNavigateToResetPassword = { email ->
                                navController.navigate(NavigationRoutes.createResetPasswordRoute(email))
                            },
                            onNavigateToRegister = {
                                navController.navigate(NavigationRoutes.REGISTER)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.RESET_PASSWORD) { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        ResetPasswordScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = loginViewModel,
                            email = email,
                            onNavigateToLogin = {
                                navController.navigate(NavigationRoutes.LOGIN, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.LOGIN, inclusive = false)
                                    .build())
                            },
                            onNavigateToHomeFromHeader = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.HOME, inclusive = false)
                                    .build())
                            },
                            onNavigateToRegister = {
                                navController.navigate(NavigationRoutes.REGISTER)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.REGISTER) {
                        RegisterScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = registerViewModel,
                            onNavigateToHome = {
                                // Navigate to home and clear the back stack to prevent back navigation
                                navController.navigate(NavigationRoutes.HOME, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.HOME, inclusive = false)
                                    .build())
                            },
                            onShowToast = { message ->
                                CustomToast.showRegistrationSuccessToast(this@MainActivity, message)
                            },
                            onNavigateToHomeFromHeader = {
                                // Navigate to home when Home button is clicked in header
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onNavigateToLogin = {
                                navController.navigate(NavigationRoutes.LOGIN)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.USER_MANAGEMENT) {
                        // Clear any form state when entering user management screen
                        LaunchedEffect(Unit) {
                            userManagementViewModel.clearFormError()
                            userManagementViewModel.clearOperationSuccess()
                        }
                        
                        UserManagementScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            viewModel = userManagementViewModel,
                            onHomeClick = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onAddUserClick = {
                                navController.navigate(NavigationRoutes.ADD_USER)
                            },
                            onEditUserClick = { userId ->
                                navController.navigate(NavigationRoutes.createEditUserRoute(userId))
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.ADD_USER) {
                        val formState by userManagementViewModel.formState.collectAsState()
                        
                        // Reset form when entering add user screen
                        LaunchedEffect(Unit) {
                            userManagementViewModel.resetForm()
                            userManagementViewModel.clearOperationSuccess()
                        }
                        
                        AddUserScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            formState = formState,
                            onNameChange = { userManagementViewModel.updateFormName(it) },
                            onEmailChange = { userManagementViewModel.updateFormEmail(it) },
                            onPasswordChange = { userManagementViewModel.updateFormPassword(it) },
                            onRoleChange = { userManagementViewModel.updateFormRole(it) },
                            onStatusChange = { userManagementViewModel.updateFormStatus(it) },
                            onImageSelected = { userManagementViewModel.updateFormImage(it) },
                            onSaveUser = { 
                                userManagementViewModel.saveUser()
                            },
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onShowToast = { message ->
                                CustomToast.showRegistrationSuccessToast(this@MainActivity, message)
                            },
                            onNavigateToUserManagement = {
                                userManagementViewModel.clearOperationSuccess()
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.USER_MANAGEMENT, inclusive = false)
                                    .build())
                            },
                            onProfileClick = {
                                navController.navigate(NavigationRoutes.PROFILE)
                            }
                        )
                    }
                    
                    composable(NavigationRoutes.EDIT_USER) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
                        val formState by userManagementViewModel.formState.collectAsState()
                        
                        // Load user data when entering edit screen
                        LaunchedEffect(userId) {
                            if (userId > 0) {
                                userManagementViewModel.loadUserForEdit(userId)
                            }
                        }
                        
                        // Navigate back to user management after successful update
                        LaunchedEffect(formState.isOperationSuccessful) {
                            if (formState.isOperationSuccessful) {
                                userManagementViewModel.clearOperationSuccess()
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.USER_MANAGEMENT, inclusive = false)
                                    .build())
                            }
                        }
                        
                        EditUserScreen(
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            userId = userId,
                            formState = formState,
                            onNameChange = { userManagementViewModel.updateFormName(it) },
                            onEmailChange = { userManagementViewModel.updateFormEmail(it) },
                            onPasswordChange = { userManagementViewModel.updateFormPassword(it) },
                            onRoleChange = { userManagementViewModel.updateFormRole(it) },
                            onStatusChange = { userManagementViewModel.updateFormStatus(it) },
                            onImageSelected = { userManagementViewModel.updateFormImage(it) },
                            onUpdateUser = { 
                                userManagementViewModel.saveUser()
                            },
                            onLoadUser = { id ->
                                userManagementViewModel.loadUserForEdit(id)
                            },
                            onNavigateToHome = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            currentUser = loginUiState.currentUser,
                            isLoggedIn = loginUiState.isLoggedIn,
                            onProfileClick = { navController.navigate(NavigationRoutes.PROFILE) }
                        )
                    }
                    
                    composable(NavigationRoutes.PROFILE) {
                        // Load user profile when entering profile screen
                        LaunchedEffect(loginUiState.currentUser?.id) {
                            loginUiState.currentUser?.id?.let { userId ->
                                profileViewModel.loadUserProfile(userId)
                            }
                        }
                        
                        ProfileScreen(
                            profileViewModel = profileViewModel,
                            currentUser = loginUiState.currentUser,
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            onHomeClick = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onEditProfileClick = {
                                navController.navigate(NavigationRoutes.EDIT_PROFILE)
                            },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            isLoggedIn = loginUiState.isLoggedIn
                        )
                    }
                    
                    composable(NavigationRoutes.EDIT_PROFILE) {
                        val profileUiState by profileViewModel.profileUiState.collectAsState()
                        val formState by profileViewModel.formState.collectAsState()
                        
                        // Load user profile and initialize form when entering edit profile screen
                        LaunchedEffect(loginUiState.currentUser?.id) {
                            loginUiState.currentUser?.id?.let { userId ->
                                profileViewModel.loadUserProfile(userId)
                            }
                        }
                        
                        // Navigate back to profile screen after successful update
                        LaunchedEffect(formState.isUpdateSuccessful) {
                            if (formState.isUpdateSuccessful) {
                                navController.navigate(NavigationRoutes.PROFILE, NavOptions.Builder()
                                    .setPopUpTo(NavigationRoutes.PROFILE, inclusive = false)
                                    .build())
                            }
                        }
                        
                        EditProfileScreen(
                            profileViewModel = profileViewModel,
                            currentUser = loginUiState.currentUser,
                            widthSizeClass = windowSizeClass.widthSizeClass,
                            onHomeClick = {
                                navController.navigate(NavigationRoutes.HOME)
                            },
                            onLogout = {
                                loginViewModel.logout()
                            },
                            onNavigateBack = {
                                navController.navigate(
                                    NavigationRoutes.PROFILE, NavOptions.Builder()
                                        .setPopUpTo(NavigationRoutes.PROFILE, inclusive = false)
                                        .build()
                                )
                            },
                            onProfileClick = { navController.navigate(NavigationRoutes.PROFILE) },
                            onUserManagementClick = {
                                navController.navigate(NavigationRoutes.USER_MANAGEMENT)
                            },
                            isLoggedIn = loginUiState.isLoggedIn
                        )
                    }
                }
            }
        }
    }
}