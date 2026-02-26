package com.example.limargent.Presentation.Configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryService: CategoryService
) : ViewModel() {

    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        loadCategories()
    }

    private fun loadCategories() {
        categories = categoryService.getCategories()
    }

    fun addCategorie(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                categoryService.addCategory(name)
                loadCategories()
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            categoryService.removeCategory(name)
            loadCategories()
        }
    }
}