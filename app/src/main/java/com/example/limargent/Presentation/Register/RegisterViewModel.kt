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
    var isLoading by mutableStateOf(false)
        private set

    fun attemptRegister(onRegisterSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = registerService.register(identifiant, motDePasse, confirmMotDePasse)

            result.onSuccess {
                isLoading = false
                onRegisterSuccess()
            }.onFailure { error ->
                isLoading = false
                errorMessage = error.message
            }
        }
    }
}
