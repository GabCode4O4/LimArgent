package com.example.limargent.Presentation.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.CategoryService
import com.example.limargent.Application.MovementsService
import com.example.limargent.Application.PreferenceService
import com.example.limargent.Data.SessionManager
import com.example.limargent.Domain.BudgetManager
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val movementsService: MovementsService,
    private val categoryService: CategoryService,
    private val preferenceService: PreferenceService,
) : ViewModel() {

    private val userN = MutableStateFlow("Utilisateur")
    val userName: StateFlow<String> = userN.asStateFlow()
    val manager = BudgetManager()

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

    private val p_toastMessage = Channel<String>()
    val toastMessage = p_toastMessage.receiveAsFlow()

    val filtrage: List<MoneyMovement>
        get() = mouvements.filter {
            (recherche.isBlank() || it.titre.contains(recherche, ignoreCase = true) || it.categorie.contains(recherche, ignoreCase = true)) &&
                    (moisFiltre == null || it.date.monthValue == moisFiltre) &&
                        (anneeFiltre == null || it.date.year == anneeFiltre) &&
                    (catFiltre == null || it.categorie == catFiltre) &&
                    (typeFiltre == null || it.type == typeFiltre)
        }.reversed()

    init {
        currentUser()
        observeData()
    }

    private fun currentUser() {
        viewModelScope.launch {
            sessionManager.currentUser.collect { firebaseUser ->
                if (firebaseUser != null) {
                    userN.value = firebaseUser.email?.split("@")?.get(0) ?: firebaseUser.uid
                } else {
                    userN.value = "Invité"
                }
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
                            val ancienBudget = budget
                            val nouveauBudget = manager.getBudget(lesMouvements).toDouble()
                            budget = nouveauBudget
                            verifierSeuilBudget(ancienBudget, nouveauBudget)
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

    private suspend fun verifierSeuilBudget(ancienBudget: Double, nouveauBudget: Double) {
        if (preferenceService.isNotificationActive()) {
            val seuil = preferenceService.getSeuilBudget()
            if (nouveauBudget < seuil && ancienBudget >= seuil) {
               p_toastMessage.send("Vous etes en dessous de votre seuil de budget !")
            }
        }
    }

    fun addMouvement(titre: String, montant: BigDecimal, categorie: String, date: LocalDate, type: MovType) {
        viewModelScope.launch {
            val mouvement = MoneyMovement(titre, montant, categorie, date, type)
            movementsService.addMouvement(mouvement)
        }
    }

    fun delMouvement(mouvement: MoneyMovement){
        viewModelScope.launch {
            movementsService.deleteMouvement(mouvement)
        }
    }


    fun resetFilters() {
        recherche = ""
        moisFiltre = null
        anneeFiltre = null
        catFiltre = null
        typeFiltre = null
    }

    fun logout() {
        sessionManager.logout()
    }
}