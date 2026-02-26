package com.example.limargent.Presentation.Configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Data.Categorie.CategoryProvider
import com.example.limargent.Data.Categorie.CategoryRepositoryImpl
import com.example.limargent.Application.CategoryService
import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.launch

class ConfigurationViewModel : ViewModel() {

    private val categoryRepository: CategoryRepository = CategoryRepositoryImpl(CategoryService(CategoryProvider()))

    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        categories = categoryRepository.getCategories()
    }

    fun addCategorie(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                categoryRepository.addCat(name)
                loadCategories()
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            categoryRepository.delCat(name)
            loadCategories()
        }
    }
}