package com.example.limargent.Presentation.Configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement

class ConfigurationViewModel: ViewModel() {
    val manager = BudgetManager()

    var budget by mutableStateOf(0.0)
        private set
    var mouvements by mutableStateOf<ArrayList<MoneyMovement>>(arrayListOf())
        private set

    init {

    }

    fun addCategorie(name : String){}
    fun delCategorie(name : String){}