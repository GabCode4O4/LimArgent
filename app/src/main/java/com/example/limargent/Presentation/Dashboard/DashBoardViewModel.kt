package com.example.limargent.Presentation.Dashboard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(private val movementsRepository: MovementsRepository): ViewModel() {
    val manager = BudgetManager()

    var budget by mutableStateOf(0.0)
        private set
    var mouvements by mutableStateOf<List<MoneyMovement>>(emptyList())
        private set

    init {
        loadData()
    }
    fun loadData(){
        viewModelScope.launch {
            val lesMouvements = movementsRepository.getMouvements()
            mouvements = lesMouvements

            val total = manager.getBudget(lesMouvements)
            budget = total.toDouble()
        }
    }
}