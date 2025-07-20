package com.example.gistaparfume.navigation

object NavigationRoutes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password/{email}"
    const val USER_MANAGEMENT = "user_management"
    const val ADD_USER = "add_user"
    const val EDIT_USER = "edit_user/{userId}"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val CATEGORY_MANAGEMENT = "category_management"
    const val ADD_CATEGORY = "add_category"
    const val EDIT_CATEGORY = "edit_category/{categoryId}"
    
    fun createResetPasswordRoute(email: String) = "reset_password/$email"
    fun createEditUserRoute(userId: Int) = "edit_user/$userId"
    fun createEditCategoryRoute(categoryId: Int) = "edit_category/$categoryId"
}