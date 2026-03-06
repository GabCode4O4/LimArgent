package com.example.limargent.Presentation.Configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Application.PreferenceService
import com.example.limargent.Domain.DataClasse.Frequence
import com.example.limargent.Domain.DataClasse.MoneyMovement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val preferenceService: PreferenceService,
    private val movementsService: MovementsService // <-- Injection du service de mouvements
) : ViewModel() {

    private val p_categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = p_categories.asStateFlow()

    private val p_theme = MutableStateFlow("system")
    val theme: StateFlow<String> = p_theme.asStateFlow()

    private val p_seuilBudget = MutableStateFlow(0.0)
    val seuilBudget: StateFlow<Double> = p_seuilBudget.asStateFlow()

    private val p_notificationActive = MutableStateFlow(false)
    val notificationActive: StateFlow<Boolean> = p_notificationActive.asStateFlow()

    private val p_MouvementRec = MutableStateFlow<List<MoneyMovement>>(emptyList())
    val MouvementRec: StateFlow<List<MoneyMovement>> = p_MouvementRec.asStateFlow()

    init {
        viewModelScope.launch {
            movementsService.getMouvementsFlow().collect { list ->
                p_MouvementRec.value = list.filter { it.isRecurrent }
            }
        }
    }

    fun loadUserPreferences() {
        loadCategories()
        loadPreference()
    }

    fun resetToDefault() {
        p_categories.value = emptyList()
        p_theme.value = "system"
        p_seuilBudget.value = 0.0
        p_notificationActive.value = false
    }

    private fun loadPreference() {
        viewModelScope.launch {
            p_theme.value = preferenceService.getTheme()
            p_seuilBudget.value = preferenceService.getSeuilBudget()
            p_notificationActive.value = preferenceService.isNotificationActive()
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

    fun setSeuilBudget(seuil: Double) {
        viewModelScope.launch {
            preferenceService.setSeuilBudget(seuil)
            loadPreference()
        }
    }

    fun setNotificationActive(active: Boolean) {
        viewModelScope.launch {
            preferenceService.setNotificationActive(active)
            loadPreference()
        }
    }

    fun stopRecurrence(mouvement: MoneyMovement) {
        viewModelScope.launch {
            val updatedMouvement = mouvement.copy(
                isRecurrent = false,
                frequence = Frequence.AUCUNE,
                nextExecutionDate = null
            )
            movementsService.updateMouvement(updatedMouvement)
        }
    }

    fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate) {
        viewModelScope.launch {
            val updatedMouvement = mouvement.copy(
                nextExecutionDate = newDate
            )
            movementsService.updateMouvement(updatedMouvement)
        }
    }
}