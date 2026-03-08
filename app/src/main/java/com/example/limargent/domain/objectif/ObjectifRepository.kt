package com.example.limargent.domain.objectif

import com.example.limargent.domain.model.ObjectifEpargne
import kotlinx.coroutines.flow.Flow

interface ObjectifRepository {
    suspend fun addObjectif(objectif: ObjectifEpargne)
    suspend fun updateObjectif(objectif: ObjectifEpargne)
    suspend fun deleteObjectif(id: String)
    fun getObjectifsFlow(): Flow<List<ObjectifEpargne>>
}
