package com.example.limargent.Presentation.Register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.RegisterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerService: RegisterService
) : ViewModel() {

    var identifiant by mutableStateOf("")
    var motDePasse by mutableStateOf("")
    var confirmMotDePasse by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun attemptRegister(onRegisterSuccess: () -> Unit) {
        if (identifiant.isBlank() || motDePasse.isBlank()) {
            errorMessage = "Veuillez remplir tous les champs."
            return
        }

        if (motDePasse != confirmMotDePasse) {
            errorMessage = "Mot de passe de confirmation incorrect"
            return
        }

        viewModelScope.launch {
            val error = registerService.register(identifiant, motDePasse)
            if (error == null) {
                errorMessage = null
                onRegisterSuccess()
            } else {
                errorMessage = error
            }
        }
    }
}