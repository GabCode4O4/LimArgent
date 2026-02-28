package com.example.limargent.Application

import com.example.limargent.Data.Movements.MovementsProvider
import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovementsService @Inject constructor(
    private val movementsProvider: MovementsProvider
) : MovementsRepository {

    override fun getMouvements(): List<MoneyMovement> {
        return movementsProvider.getMouvements()
    }

    override suspend fun addMouvement(mouvement: MoneyMovement) {
        withContext(Dispatchers.IO) {
            movementsProvider.addMouvement(mouvement)
        }
    }

    override suspend fun deleteMouvement(mouvement: MoneyMovement) {
        withContext(Dispatchers.IO) {
            movementsProvider.deleteMouvement(mouvement)
        }
    }
}