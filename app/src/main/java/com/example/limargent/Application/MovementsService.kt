package com.example.limargent.Application

import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.Mouvements.MovementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovementsService @Inject constructor(
    private val movementsRepository: MovementsRepository
) : MovementsRepository {

    override suspend fun getMouvements(): List<MoneyMovement> {
        return withContext(Dispatchers.IO) {
            movementsRepository.getMouvements()
        }
    }

    override suspend fun addMouvement(mouvement: MoneyMovement) {
        withContext(Dispatchers.IO) {
            movementsRepository.addMouvement(mouvement)
        }
    }

    override suspend fun deleteMouvement(mouvement: MoneyMovement) {
        withContext(Dispatchers.IO) {
            movementsRepository.deleteMouvement(mouvement)
        }
    }
    
    override fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return movementsRepository.getMouvementsFlow()
    }
}