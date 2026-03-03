package com.example.limargent.Presentation.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Data.SessionManager
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.Categorie.CategoryRepository
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
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
    private val movementsRepository: MovementsRepository,
    private val categoryRepository: CategoryRepository
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
        loadData()
        loadCategories()
    }

    private fun currentUser() {
        viewModelScope.launch {
            sessionManager.UserIdentifiant.collect { currentUser ->
                if (currentUser != null) {
                    _userName.value = currentUser
                } else {
                    _userName.value = "Invité"
                }
            }
        }
    }

    fun loadData(){
        viewModelScope.launch {
            val lesMouvements = movementsRepository.getMouvements()
            mouvements = lesMouvements

            val total = manager.getBudget(lesMouvements)
            budget = total.toDouble()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categories = categoryRepository.getCategories()
        }
    }

    fun addMouvement(titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) {
        viewModelScope.launch {
            val mouvement = MoneyMovement(titre, montant, categorie, date, type)
            movementsRepository.addMouvement(mouvement)
            loadData()
        }
    }

    fun delMouvement(mouvement: MoneyMovement){
        viewModelScope.launch {
            movementsRepository.deleteMouvement(mouvement)
            loadData()
        }
    }

    fun logout() {
        sessionManager.logout()
    }
}