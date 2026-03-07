package com.example.limargent.domain.mouvements

import com.example.limargent.domain.dataClasse.MoneyMovement
import kotlinx.coroutines.flow.Flow

interface MovementsRepository {
    suspend fun addMouvement(mouvement: MoneyMovement): Result<Unit>
    suspend fun deleteMouvement(mouvement: MoneyMovement): Result<Unit>
    suspend fun updateMouvement(mouvement: MoneyMovement): Result<Unit>
    fun getMouvementsFlow(): Flow<List<MoneyMovement>>
}