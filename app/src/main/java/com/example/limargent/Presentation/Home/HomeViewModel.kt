package com.example.limargent.Presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.limargent.Data.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userName = MutableStateFlow("Utilisateur")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        currentUser()
    }

    private fun currentUser() {
        viewModelScope.launch {
            sessionManager.UserIdentifiant.collect { currentUser ->
                if (currentUser != null) {
                    _userName.value = currentUser
                } else {
                    _userName.value = "Invité"
                }
            }
        }
    }

    fun logout() {
        sessionManager.logout()
    }
}