package com.example.limargent.Domain.Objectif

import com.example.limargent.Domain.DataClasse.ObjectifEpargne
import kotlinx.coroutines.flow.Flow

interface ObjectifRepository {
    suspend fun addObjectif(objectif: ObjectifEpargne)
    suspend fun updateObjectif(objectif: ObjectifEpargne)
    suspend fun deleteObjectif(id: String)
    fun getObjectifsFlow(): Flow<List<ObjectifEpargne>>
}