package com.example.limargent.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.application.LoginService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: LoginService
) : ViewModel() {
    var identifiant by mutableStateOf("")
    var motDePasse by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun onLog(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = loginService.authentification(identifiant, motDePasse)

            result.onSuccess {
                isLoading = false
                onLoginSuccess()
            }.onFailure { error ->
                isLoading = false
                errorMessage = error.message
            }
        }
    }
    fun clearError() {
        errorMessage = null
    }
    fun clearCredentials() {
        identifiant = ""
        motDePasse = ""
        errorMessage = null
    }
}
