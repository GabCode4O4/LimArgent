package com.example.limargent.Data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val UserId = MutableStateFlow<String?>(null)
    val UserIdentifiant: StateFlow<String?> =UserId.asStateFlow()

    fun login(identifiant: String) {
        UserId.value = identifiant
    }

    fun logout() {
        UserId.value = null
    }

    fun isLoggedIn(): Boolean {
        return UserId.value != null
    }
}