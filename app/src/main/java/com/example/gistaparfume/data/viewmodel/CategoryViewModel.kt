package com.example.gistaparfume.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gistaparfume.data.Category
import com.example.gistaparfume.data.config.AppDatabaseConfig
import com.example.gistaparfume.data.repository.CategoryRepository
import com.example.gistaparfume.data.toUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CategoryRepository(
        AppDatabaseConfig.getDatabase(app).categoryDao()
    )

    val categories: StateFlow<List<Category>> = repo.getAllCategories()
        .map { entityList ->
            entityList.map { it.toUI() } // Map entities to UI models
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}