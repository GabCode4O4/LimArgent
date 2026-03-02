package com.example.limargent.Presentation.Login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Application.LoginService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginService: LoginService
) : ViewModel() {

    var identifiant by mutableStateOf("")
    var motDePasse by mutableStateOf("")
    var isError by mutableStateOf(false)
        private set

    fun onLog(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = loginService.authentification(identifiant, motDePasse)
            if (success) {
                isError = false
                onLoginSuccess()
            } else {
                isError = true
            }
        }
    }
}