package com.example.limargent.Data.Movements

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.MoneyMovement
import javax.inject.Inject

class MovementsProvider @Inject constructor(
    private val userProvider: UserProvider
) {
    // TODO: Gérer l'utilisateur courant (session)
    private val currentUserIdentifiant = "default" // Pour l'instant, on utilise un utilisateur par défaut

    fun getMouvements(): List<MoneyMovement> {
        val user = userProvider.getUser(currentUserIdentifiant)
        return user?.mouvements ?: emptyList()
    }

    fun addMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserIdentifiant)
        if (user != null) {
            user.mouvements.add(mouvement)
            userProvider.saveUser(user)
        }
    }

    fun deleteMouvement(mouvement: MoneyMovement) {
        val user = userProvider.getUser(currentUserIdentifiant)
        if (user != null) {
            user.mouvements.remove(mouvement)
            userProvider.saveUser(user)
        }
    }
}