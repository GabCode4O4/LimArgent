package com.example.limargent.presentation.objectif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.application.ObjectifService
import com.example.limargent.domain.model.ObjectifEpargne
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ObjectifViewModel @Inject constructor(
    private val objectifService: ObjectifService
) : ViewModel() {

    private val p_objectifs = MutableStateFlow<List<ObjectifEpargne>>(emptyList())
    val objectifs: StateFlow<List<ObjectifEpargne>> = p_objectifs.asStateFlow()
    private val p_toastMessage = Channel<String>()
    val toastMessage = p_toastMessage.receiveAsFlow()

    init {
        viewModelScope.launch{
            objectifService.getObjectifsFlow()
                .catch { error -> p_toastMessage.send(error.message ?: "Erreur de chargement") }
                .collect { liste ->
                    p_objectifs.value = liste
                }
        }
    }

    fun saveObjectif(objectif: ObjectifEpargne){
        viewModelScope.launch {
            val result = objectifService.saveObjectif(objectif)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur d'enregistrement")
            }
        }
    }

    fun addProgress(id: String, montantAjoute: BigDecimal) {
        viewModelScope.launch {
            val result = objectifService.addProgres(id, montantAjoute)
            result.onFailure { error ->
                p_toastMessage.send(error.message ?: "Erreur de mise à jour")
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            objectifService.deleteObjectif(id)
        }
    }
}