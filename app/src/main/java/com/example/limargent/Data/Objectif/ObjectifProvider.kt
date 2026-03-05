package com.example.limargent.Data.Objectifs

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.ObjectifEpargne
import com.example.limargent.Domain.Objectif.ObjectifRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectifProvider @Inject constructor(
    private val userProvider: UserProvider
) : ObjectifRepository {

    override suspend fun addObjectif(objectif: ObjectifEpargne) {
        userProvider.addObjectif(objectif)
    }

    override suspend fun updateObjectif(objectif: ObjectifEpargne) {
        userProvider.updateObjectif(objectif)
    }

    override suspend fun deleteObjectif(id: String) {
        userProvider.deleteObjectif(id)
    }

    override fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> {
        return userProvider.getObjectifsFlow()
    }
}
