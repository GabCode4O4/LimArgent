package com.example.limargent.Presentation.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Application.PreferenceService
import com.example.limargent.Data.Classe.SessionManager
import com.example.limargent.Domain.DataClasse.MoneyMovement
import com.example.limargent.Domain.DataClasse.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val movementsService: MovementsService,
    private val categoryService: CategoryService,
    private val preferenceService: PreferenceService,
) : ViewModel() {

    private val p_userName = MutableStateFlow("Utilisateur")
    val userName: StateFlow<String> = p_userName.asStateFlow()

    var budget by mutableStateOf(0.0)
        private set
    var mouvements by mutableStateOf<List<MoneyMovement>>(emptyList())
        private set
    var categories by mutableStateOf<List<String>>(emptyList())
        private set
    var recherche by mutableStateOf("")
    var moisFiltre by mutableStateOf<Int?>(null)
    var anneeFiltre by mutableStateOf<Int?>(null)
    var catFiltre by mutableStateOf<String?>(null)
    var typeFiltre by mutableStateOf<MovType?>(null)

    var mouvementEnEdition by mutableStateOf<MoneyMovement?>(null)
        private set

    private val p_toastMessage = Channel<String>()
    val toastMessage = p_toastMessage.receiveAsFlow()

    val filtrage: List<MoneyMovement>
        get() = mouvements.filter {
            (recherche.isBlank() || it.titre.contains(recherche, ignoreCase = true) || it.categorie.contains(recherche, ignoreCase = true)) &&
                    (moisFiltre == null || it.date.monthValue == moisFiltre) &&
                    (anneeFiltre == null || it.date.year == anneeFiltre) &&
                    (catFiltre == null || it.categorie == catFiltre) &&
                    (typeFiltre == null || it.type == typeFiltre)
        }.sortedByDescending { it.date }

    init {
        currentUser()
        observeData()
    }

    private fun currentUser() {
        viewModelScope.launch {
            sessionManager.currentUser.collect { firebaseUser ->
                p_userName.value = firebaseUser?.email?.split("@")?.get(0) ?: firebaseUser?.uid ?: "Invité"
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            sessionManager.currentUser.collect { user ->
                if (user != null) {
                    launch {
                        movementsService.getMouvementsFlow().collect { lesMouvements ->
                            mouvements = lesMouvements
                            budget = lesMouvements.sumOf {
                                if (it.type == MovType.entree) it.montant.toDouble() else -it.montant.toDouble()
                            }
                        }
                    }
                    launch {
                        categoryService.getCategoriesFlow().collect { lesCategories ->
                            categories = lesCategories
                        }
                    }
                } else {
                    mouvements = emptyList()
                    categories = emptyList()
                    budget = 0.0
                }
            }
        }
    }

    fun onEditMouvement(mouvement: MoneyMovement) {
        mouvementEnEdition = mouvement
    }

    fun onDismissMouvement() {
        mouvementEnEdition = null
    }

    fun saveMouvement(mouvement: MoneyMovement) {
        viewModelScope.launch {
            val result = movementsService.saveMouvement(mouvement)
            result.onSuccess {
                p_toastMessage.send("Mouvement enregistré !")
                onDismissMouvement()
            }.onFailure { error ->
                p_toastMessage.send(error.message ?: "Une erreur est survenue.")
            }
        }
    }

    fun delMouvement(mouvement: MoneyMovement) {
        viewModelScope.launch {
            val result = movementsService.deleteMouvement(mouvement)
            result.onSuccess {
                p_toastMessage.send("Mouvement supprimé.")
            }.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur lors de la suppression.")
            }
        }
    }

    fun resetFilters() {
        recherche = ""
        moisFiltre = null
        anneeFiltre = null
        catFiltre = null
        typeFiltre = null
    }
}