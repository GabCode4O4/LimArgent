package com.example.limargent.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.application.classe.BudgetManager
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.model.MovType
import com.example.limargent.domain.mouvements.MouvementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    mouvementRepository: MouvementRepository,
    private val budgetManager: BudgetManager
) : ViewModel() {

    val mouvements: StateFlow<List<MoneyMovement>> =
        mouvementRepository
            .getMouvementsFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
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
        var currentBalance = 0f

        for (m in sortedMovements) {
            val amount = m.montant.toFloat()
            if (m.type == MovType.entree) {
                currentBalance += amount
            } else {
                currentBalance -= amount
            }
            result.add(m.date to currentBalance)
        }
        return result
    }
}
