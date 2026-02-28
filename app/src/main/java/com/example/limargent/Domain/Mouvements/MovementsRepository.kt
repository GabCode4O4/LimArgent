package com.example.limargent.Domain.Mouvements

import com.example.limargent.Domain.MoneyMovement

interface MovementsRepository {
    fun getMouvements(): List<MoneyMovement>
    suspend fun addMouvement(mouvement: MoneyMovement)
    suspend fun deleteMouvement(mouvement: MoneyMovement)
}