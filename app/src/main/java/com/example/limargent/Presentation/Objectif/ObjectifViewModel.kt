package com.example.limargent.Presentation.Objectif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.ObjectifService
import com.example.limargent.Domain.ObjectifEpargne
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ObjectifViewModel @Inject constructor(
    private val objectifService: ObjectifService
) : ViewModel() {

    private val p_objectifs = MutableStateFlow<List<ObjectifEpargne>>(emptyList())
    val objectifs: StateFlow<List<ObjectifEpargne>> = p_objectifs.asStateFlow()

    init {
        viewModelScope.launch {
            objectifService.getObjectifsFlow()
                .catch {}
                .collect { liste ->
                    p_objectifs.value = liste
                }
        }
    }

    fun ajouterObjectif(nom: String, montantCible: Double, dateEcheance: LocalDate? = null) {
        viewModelScope.launch {
            val nouvelObjectif = ObjectifEpargne(
                id = UUID.randomUUID().toString(),
                nom = nom,
                montantCible = montantCible,
                dateEcheance = dateEcheance
            )
            objectifService.addObjectif(nouvelObjectif)
        }
    }

    fun mettreAJourEpargne(id: String, montantAjoute: Double) {
        viewModelScope.launch {
            val objectif = p_objectifs.value.find { it.id == id }
            if (objectif != null) {
                val nouveauMontant = (objectif.montantActuel + montantAjoute).coerceAtMost(objectif.montantCible)
                objectifService.updateObjectif(objectif.copy(montantActuel = nouveauMontant))
            }
        }
    }

    fun supprimerObjectif(id: String) {
        viewModelScope.launch {
            objectifService.deleteObjectif(id)
        }
    }
}