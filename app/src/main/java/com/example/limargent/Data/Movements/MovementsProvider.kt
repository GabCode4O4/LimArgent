package com.example.limargent.Data.Movements

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovementsProvider @Inject constructor(
    private val userProvider: UserProvider
) : MovementsRepository {
    override suspend fun getMouvements(): List<MoneyMovement> {
        return userProvider.getMouvements()
    }

    override suspend fun addMouvement(mouvement: MoneyMovement) {
        userProvider.addMovement(mouvement)
    }

    override suspend fun deleteMouvement(mouvement: MoneyMovement) {
        mouvement.id?.let { userProvider.deleteMovement(it) }
    }
    override fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return userProvider.getMouvementsFlow()
    }
}