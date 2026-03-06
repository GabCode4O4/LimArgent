package com.example.limargent.Presentation.Configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Application.PreferenceService
import com.example.limargent.Domain.DataClasse.MoneyMovement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val preferenceService: PreferenceService,
    private val movementsService: MovementsService
) : ViewModel() {

    private val p_categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = p_categories.asStateFlow()

    private val p_theme = MutableStateFlow("system")
    val theme: StateFlow<String> = p_theme.asStateFlow()

    private val p_seuilBudget = MutableStateFlow(0.0)
    val seuilBudget: StateFlow<Double> = p_seuilBudget.asStateFlow()

    private val p_notificationActive = MutableStateFlow(false)
    val notificationActive: StateFlow<Boolean> = p_notificationActive.asStateFlow()

    private val p_mouvementRec = MutableStateFlow<List<MoneyMovement>>(emptyList())
    val mouvementRec: StateFlow<List<MoneyMovement>> = p_mouvementRec.asStateFlow()

    private val p_toastMessage = Channel<String>()
    val toastMessage = p_toastMessage.receiveAsFlow()

    init {
        loadUserPreferences()
        observeRecurrentMovements()
    }

    private fun observeRecurrentMovements() {
        viewModelScope.launch {
            movementsService.getMouvementsFlow().collect { list ->
                p_mouvementRec.value = list.filter { it.isRecurrent }
            }
        }
    }

    fun loadUserPreferences() {
        observeCategories()
        loadPreference()
    }

    private fun loadPreference() {
        viewModelScope.launch {
            p_theme.value = preferenceService.getTheme()
            p_seuilBudget.value = preferenceService.getSeuilBudget()
            p_notificationActive.value = preferenceService.isNotificationActive()
        }
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryService.getCategoriesFlow().collect {
                p_categories.value = it
            }
        }
    }

    fun addCategorie(name: String) {
        viewModelScope.launch {
            val result = categoryService.addCategorie(name)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur inconnue")
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            val result = categoryService.supprimer(name)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur inconnue")
            }
        }
    }

    fun setThemePreference(theme: String) {
        viewModelScope.launch {
            preferenceService.setTheme(theme).onSuccess {
                loadPreference()
            }
        }
    }

    fun setSeuilBudget(seuil: Double) {
        viewModelScope.launch {
            preferenceService.setSeuilBudget(seuil)
                .onSuccess { loadPreference() }
                .onFailure { error -> p_toastMessage.send(error.message ?: "Erreur") }
        }
    }

    fun setNotificationActive(active: Boolean) {
        viewModelScope.launch {
            preferenceService.setNotificationActive(active).onSuccess {
                loadPreference()
            }
        }
    }

    fun stopRecurrence(mouvement: MoneyMovement) {
        viewModelScope.launch {
            val result = movementsService.stopRecurrence(mouvement)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur")
            }
        }
    }

    fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate) {
        viewModelScope.launch {
            val result = movementsService.updateRecurrenceDate(mouvement, newDate)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur")
            }
        }
    }
}
