package com.example.limargent.Domain.Mouvements

import com.example.limargent.Domain.DataClasse.MoneyMovement

interface MovementsRepository {
    suspend fun getMouvements(): List<MoneyMovement>
    suspend fun addMouvement(mouvement: MoneyMovement)
    suspend fun deleteMouvement(mouvement: MoneyMovement)
    suspend fun updateMouvement(mouvement: MoneyMovement)
    fun getMouvementsFlow(): kotlinx.coroutines.flow.Flow<List<MoneyMovement>>

}