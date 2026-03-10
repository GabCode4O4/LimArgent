package com.example.limargent.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.application.classe.BudgetManager
import com.example.limargent.application.classe.SessionManager
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.model.MovType
import com.example.limargent.domain.mouvements.MouvementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// Quand le viewmodel est créé, il créé en cascade ses dépendances (il est créé dans MainScreen.kt)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    mouvementRepository: MouvementRepository,
    private val budgetManager: BudgetManager,
    private val sessionManager: SessionManager
) : ViewModel() {
    // on récupère les mouvements avec une version modifiable mutable state flow, ce qui permet de faire une étape intermédiaire au cas où l'utilisateur se déconnecte
    //, si l'utilisateur se déco, on peut vider la variable sans toucher au database
    private val p_mouvements = MutableStateFlow<List<MoneyMovement>>(ArrayList())
    // et le paramètre en lecture seule qu'on va utiliser pour l'UI
    val mouvements: StateFlow<List<MoneyMovement>> = p_mouvements
    init {
        viewModelScope.launch {
            sessionManager.currentUser.collectLatest { user ->
                if (user != null) {
                    mouvementRepository.getMouvementsFlow().collect { liste ->
                        p_mouvements.value = liste
                    }
                } else {
                    p_mouvements.value = emptyList()
                }
            }
        }
    }
    fun getCategorieParType(type: MovType): Map<String, Float> {
        return budgetManager.getBudgetCategorie(mouvements.value, type)
    }

    fun getCategorieParCat(category: String): Map<String, Float> {
        return budgetManager.getSousCategories(mouvements.value, category)
    }

    fun getBudgetByDateList(): List<Pair<LocalDate, Float>> {
        val list = mouvements.value
        if (list.isEmpty()) return emptyList()

        val sortedMovements = list.sortedBy { it.date }
        val result = mutableListOf<Pair<LocalDate, Float>>()
        var budget = 0f

        for (m in sortedMovements) {
            val montant = m.montant.toFloat()
            if (m.type == MovType.entree) {
                budget += montant
            } else {
                budget -= montant
            }
            result.add(m.date to budget)
        }
        return result
    }
}
