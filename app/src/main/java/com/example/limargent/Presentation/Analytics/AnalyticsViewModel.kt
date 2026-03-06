package com.example.limargent.Presentation.Analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.DataClasse.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import com.example.limargent.Domain.DataClasse.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val movementsRepository : MovementsRepository
) : ViewModel() {

    val mouvements: StateFlow<List<MoneyMovement>> =
        movementsRepository
            .getMouvementsFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun getCategoriesData(type: MovType): Map<String, Float> {
        val list = mouvements.value
        val chartData = mutableMapOf<String, Float>()

        for (m in list) {
            if (m.type != type) continue
            chartData[m.categorie] =
                (chartData[m.categorie] ?: 0f) + m.montant.toFloat()
        }

        return chartData.entries
            .sortedByDescending { it.value }
            .associate { it.toPair() }
    }

    fun getCategoryData(category: String) : Map<String, Float>{
        val list = mouvements.value
        val chartData = mutableMapOf<String, Float>()

        for (m in list) {
            if (m.categorie != category) continue
            chartData[m.titre] = (chartData[m.titre] ?: 0f) + m.montant.toFloat()
        }

        return chartData.entries
            .sortedByDescending { it.value }
            .associate { it.toPair() }
    }

    fun getBudgetByDateList(): List<Pair<LocalDate, Float>> {
        val sortedMovements = mouvements.value.sortedBy { it.date }

        val budgetMap = mutableMapOf<LocalDate, Float>()
        var tempM = 0f

        for (m in sortedMovements) {
            if (m.type == MovType.entree) tempM += m.montant.toFloat()
            else tempM -= m.montant.toFloat()

            budgetMap[m.date] = tempM  // écrase si la date existe déjà
        }

        return budgetMap.toList()
    }

    // 67
}