package com.example.limargent.Data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Doit être un Singleton pour partager la même session partout
class SessionManager @Inject constructor() {
    var currentUserIdentifiant: String? = null
        private set

    fun login(identifiant: String) {
        currentUserIdentifiant = identifiant
    }

    fun logout() {
        currentUserIdentifiant = null
    }

    fun isLoggedIn(): Boolean {
        return currentUserIdentifiant != null
    }
}