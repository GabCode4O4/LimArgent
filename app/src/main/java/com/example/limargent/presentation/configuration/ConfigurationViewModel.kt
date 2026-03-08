package com.example.limargent.presentation.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.application.CategoryService
import com.example.limargent.application.MouvementService
import com.example.limargent.application.PreferenceService
import com.example.limargent.application.classe.SessionManager
import com.example.limargent.domain.model.MoneyMovement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryService: CategoryService,
    private val preferenceService: PreferenceService,
    private val mouvementService: MouvementService,
    private val sessionManager: SessionManager
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
        obsMouvementRec()
    }

    private fun obsMouvementRec() {
        viewModelScope.launch {
            sessionManager.currentUser.collectLatest { user ->
                if (user != null) {
                    mouvementService.getMouvementsFlow().collect { list ->
                        p_mouvementRec.value = list.filter { it.isRecurrent }
                    }
                } else {
                    p_mouvementRec.value = emptyList()
                }
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
            p_theme.value = theme
            preferenceService.setTheme(theme)
        }
    }

    fun setSeuilBudget(seuil: Double) {
        viewModelScope.launch {
            p_seuilBudget.value = seuil
            preferenceService.setSeuilBudget(seuil).onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur")
            }
        }
    }

    fun setNotificationActive(active: Boolean) {
        viewModelScope.launch {
            p_notificationActive.value = active
            preferenceService.setNotificationActive(active)
        }
    }

    fun stopRecurrence(mouvement: MoneyMovement) {
        viewModelScope.launch {
            val result = mouvementService.stopRecurrence(mouvement)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur")
            }
        }
    }

    fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate) {
        viewModelScope.launch {
            val result = mouvementService.updateRecurrenceDate(mouvement, newDate)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur")
            }
        }
    }
}
