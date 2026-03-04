package com.example.limargent.Presentation.Configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.PreferenceService
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
    private val categoryService: CategoryService,      // Modifié ici
    private val preferenceService: PreferenceService
) : ViewModel() {

    private val p_categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = p_categories.asStateFlow()

    private val p_theme = MutableStateFlow("system")
    val theme: StateFlow<String> = p_theme.asStateFlow()

    fun loadUserPreferences() {
        loadCategories()
        loadPreference()
    }

    fun resetToDefault() {
        p_categories.value = emptyList()
        p_theme.value = "system"
    }

    private fun loadPreference() {
        viewModelScope.launch {
            p_theme.value = preferenceService.getTheme()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            p_categories.value = categoryService.getCategories()
        }
    }

    fun addCategorie(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                categoryService.addCategorie(name)
                loadCategories()
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            categoryService.supprimer(name)
            loadCategories()
        }
    }

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            preferenceService.setTheme(theme)
            loadPreference()
        }
    }
}