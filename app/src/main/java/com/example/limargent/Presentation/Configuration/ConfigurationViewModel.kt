package com.example.limargent.Presentation.Configuration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Domain.Categorie.CategoryRepository
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import com.example.limargent.Domain.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val movementsRepository: MovementsRepository
) : ViewModel() {

    var categories by mutableStateOf<List<String>>(emptyList())
        private set
    var mouvement by mutableStateOf<List<MoneyMovement>>(emptyList())

    init {
        loadCategories()
        loadMouvements()

    }
    private fun loadMouvements(){
        mouvement = movementsRepository.getMouvements()

    }

    fun addMouvement(titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) {
        viewModelScope.launch {
            val mouvement = MoneyMovement(titre, montant, categorie, date, type)
            movementsRepository.addMouvement(mouvement)
            loadMouvements()
        }

    }

    fun delMouvement(mouvement: MoneyMovement){
        viewModelScope.launch {
            movementsRepository.deleteMouvement(mouvement)
            loadMouvements()
        }
    }

    private fun loadCategories() {
        categories = categoryRepository.getCategories()
    }

    fun addCategorie(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                categoryRepository.addCategorie(name)
                loadCategories()
            }
        }
    }

    fun delCategorie(name: String) {
        viewModelScope.launch {
            categoryRepository.supprimerCat(name)
            loadCategories()
        }
    }
}