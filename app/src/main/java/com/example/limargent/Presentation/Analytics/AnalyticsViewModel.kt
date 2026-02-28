package com.example.limargent.Presentation.Analytics

import androidx.compose.runtime.getValue
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
        mouvements = movementsRepository.getMouvements()
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

    // 67
}