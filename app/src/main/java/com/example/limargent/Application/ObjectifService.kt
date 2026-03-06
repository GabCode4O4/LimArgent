package com.example.limargent.Application

import com.example.limargent.Domain.DataClasse.ObjectifEpargne
import com.example.limargent.Domain.Objectif.ObjectifRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ObjectifService @Inject constructor(
    private val objectifRepository: ObjectifRepository
) {
    suspend fun addObjectif(objectif: ObjectifEpargne) = withContext(Dispatchers.IO) {
        objectifRepository.addObjectif(objectif)
    }

    suspend fun updateObjectif(objectif: ObjectifEpargne) = withContext(Dispatchers.IO) {
        objectifRepository.updateObjectif(objectif)
    }

    suspend fun deleteObjectif(id: String) = withContext(Dispatchers.IO) {
        objectifRepository.deleteObjectif(id)
    }

    fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> {
        return objectifRepository.getObjectifsFlow()
    }
}