package com.example.limargent.Presentation.Configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.Categorie.CategoryRepository
import com.example.limargent.Domain.Preference.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _theme = MutableStateFlow("system")
    val theme: StateFlow<String> = _theme.asStateFlow()

    fun loadUserPreferences() {
        loadCategories()
        loadPreference()
    }

    fun resetToDefault() {
        _categories.value = emptyList()
        _theme.value = "system"
    }

    private fun loadPreference() {
        viewModelScope.launch {
            _theme.value = preferenceRepository.getTheme()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = categoryRepository.getCategories()
        }
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

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            preferenceRepository.setTheme(theme)
            loadPreference()
        }
    }
}