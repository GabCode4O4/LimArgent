package com.example.limargent.Presentation.Objectif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.ObjectifService
import com.example.limargent.Domain.DataClasse.ObjectifEpargne
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

    fun addObjectif(nom: String, montantCible: Double, dateEcheance: LocalDate? = null, tauxInteret: Double = 0.0) {
        viewModelScope.launch {
            val nouvelObjectif = ObjectifEpargne(
                id = UUID.randomUUID().toString(),
                nom = nom,
                montantCible = montantCible,
                dateEcheance = dateEcheance,
                tauxInteret = tauxInteret 
            )
            objectifService.addObjectif(nouvelObjectif)
        }
    }

    fun update(id: String, montantAjoute: Double) {
        viewModelScope.launch {
            val objectif = p_objectifs.value.find { it.id == id }
            if (objectif != null) {
                val nouveauMontant = (objectif.montantActuel + montantAjoute).coerceAtMost(objectif.montantCible)
                objectifService.updateObjectif(objectif.copy(montantActuel = nouveauMontant))
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            objectifService.deleteObjectif(id)
        }
    }
}