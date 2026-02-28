package com.example.limargent.Presentation.Configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.Categorie.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

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
                categoryRepository.addCategorie(name)
                loadCategories()
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            categoryRepository.supprimerCat(name)
            loadCategories()
        }
    }
}