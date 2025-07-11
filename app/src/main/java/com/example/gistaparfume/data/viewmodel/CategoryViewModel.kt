package com.example.gistaparfume.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.repository.CategoryRepository
import com.example.gistaparfume.data.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CategoryRepository(
        AppDatabaseConfig.getDatabase(app).categoryDao()
    )

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    fun loadCategories() = viewModelScope.launch {
        val list = repo.getAllCategories()
            .map { it.toUI() }
        _categories.value = list
    }
}