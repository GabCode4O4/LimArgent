package com.example.limargent.Data.Movements

import android.content.Context
import com.example.limargent.Data.SessionManager
import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.MoneyMovement
import javax.inject.Inject

class MovementsProvider @Inject constructor(
    private val userProvider: UserProvider,
    private val sessionManager: SessionManager // Injection du SessionManager
) {
    // On récupère dynamiquement l'identifiant, ou "default" par sécurité s'il y a un souci
    private val currentUserId: String
        get() = sessionManager.currentUserIdentifiant ?: "default"

    fun getMouvements(): List<MoneyMovement> {
        val user = userProvider.getUser(currentUserId)
        return user?.mouvements?.toList() ?: emptyList()
    }

    fun addMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserId)
        if (user != null) {
            user.mouvements.add(mouvement)
            userProvider.saveUser(user)
        }
    }

    fun deleteMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserId)
        if (user != null) {
            user.mouvements.remove(mouvement)
            userProvider.saveUser(user)
        }
    }
}