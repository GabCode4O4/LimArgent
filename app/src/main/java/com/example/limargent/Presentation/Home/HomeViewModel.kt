package com.example.limargent.Presentation.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Data.SessionManager
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val movementsService: MovementsService,
    private val categoryService: CategoryService
) : ViewModel() {

    private val _userName = MutableStateFlow("Utilisateur")
    val userName: StateFlow<String> = _userName.asStateFlow()
    val manager = BudgetManager()
    var budget by mutableStateOf(0.0)
        private set
    var mouvements by mutableStateOf<List<MoneyMovement>>(emptyList())
        private set
    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        currentUser()
        observeData() // On lance l'écoute des données dès la création du ViewModel
    }

    private fun currentUser() {
        viewModelScope.launch {
            sessionManager.currentUser.collect { firebaseUser ->
                if (firebaseUser != null) {
                    _userName.value = firebaseUser.email?.split("@")?.get(0) ?: firebaseUser.uid
                } else {
                    _userName.value = "Invité"
                }
            }
        }
    }

    private fun observeData() {
        // 1. On écoute les mouvements en continu
        viewModelScope.launch {
            movementsService.getMouvementsFlow().collect { lesMouvements ->
                mouvements = lesMouvements
                budget = manager.getBudget(lesMouvements).toDouble()
            }
        }

        // 2. On écoute les catégories en continu
        viewModelScope.launch {
            categoryService.getCategoriesFlow().collect { lesCategories ->
                categories = lesCategories
            }
        }
    }

    fun addMouvement(titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) {
        viewModelScope.launch {
            val mouvement = MoneyMovement(titre, montant, categorie, date, type)
            movementsService.addMouvement(mouvement)
        }
    }

    fun delMouvement(mouvement: MoneyMovement){
        viewModelScope.launch {
            movementsService.deleteMouvement(mouvement)
        }
    }

    fun logout() {
        sessionManager.logout()
    }
}