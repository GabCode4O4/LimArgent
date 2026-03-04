package com.example.limargent.Presentation.Analytics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import com.example.limargent.Domain.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val movementsRepository : MovementsRepository
) : ViewModel() {

    var mouvements by mutableStateOf<List<MoneyMovement>>(emptyList())
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            mouvements = movementsRepository.getMouvements()
        }
    }

    fun getCategoriesData(type: MovType) : Map<String, Float>{
        val chartData = mutableMapOf<String, Float>()

        for (m in mouvements) {
            if (m.type != type) continue // Si c'est pas le type de mouvement qu'on veut on l'ignore
            // On ajoute le montant à la catégorie si elle existe déjà sinon on la créé dans une map
            chartData[m.categorie] = (chartData[m.categorie] ?: 0f) + m.montant.toFloat()
        }

        return chartData
    }

    fun getCategoryData(category: String) : Map<String, Float>{
        val chartData = mutableMapOf<String, Float>()

        for (m in mouvements) {
            if (m.categorie != category) continue
            chartData[m.titre] = (chartData[m.titre] ?: 0f) + m.montant.toFloat()
        }

        return chartData
    }

    fun getBudgetByDateList(): List<Pair<LocalDate, Float>> {
        val sortedMovements = mouvements.sortedBy { it.date }
        val budget = mutableListOf<Pair<LocalDate, Float>>()

        var tempM = 0f

        for (m in sortedMovements) {
            if (m.type == MovType.entree) {
                tempM+=m.montant.toFloat()
            } else {
                tempM-=m.montant.toFloat()
            }
            budget.add(m.date to tempM)
        }

        return budget
    }

    // 67
}