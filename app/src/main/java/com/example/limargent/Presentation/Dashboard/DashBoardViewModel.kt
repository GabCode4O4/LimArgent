package com.example.limargent.Presentation.Dashboard
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement


class DashBoardViewModel: ViewModel() {
    val manager = BudgetManager()

    var budget by mutableStateOf(0.0)
        private set
    var mouvements by mutableStateOf<ArrayList<MoneyMovement>>(arrayListOf())
        private set

    init {
        loadData()
    }

    fun loadData(){
        val total = manager.getBudget()
        budget = total.toDouble()
        val categories = manager.getBudgetCategorie()
        mouvements = manager.getMov()
    }
}