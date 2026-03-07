package com.example.limargent.application

import com.example.limargent.domain.dataClasse.ObjectifEpargne
import com.example.limargent.domain.objectif.ObjectifRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class ObjectifService @Inject constructor(
    private val objectifRepository: ObjectifRepository
) {

    fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> {
        return objectifRepository.getObjectifsFlow()
    }

    suspend fun saveObjectif(objectif: ObjectifEpargne): Result<Unit> = withContext(Dispatchers.IO) {
        if (objectif.nom.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Le nom de l'objectif ne peut pas être vide."))
        }
        if (objectif.montantCible <= 0) {
            return@withContext Result.failure(IllegalArgumentException("Le montant cible doit être positif."))
        }
        if (objectif.dateEcheance != null && objectif.dateEcheance.isBefore(LocalDate.now())) {
            return@withContext Result.failure(IllegalArgumentException("La date d'échéance ne peut pas être dans le passé."))
        }
        val finalObjectif = if (objectif.id.isBlank()) {
            objectif.copy(id = UUID.randomUUID().toString())
        } else {
            objectif
        }

        objectifRepository.updateObjectif(finalObjectif)
        Result.success(Unit)
    }

    suspend fun addProgress(id: String, montantAjoute: Double): Result<Unit> = withContext(Dispatchers.IO) {
        if (montantAjoute <= 0) {
            return@withContext Result.failure(IllegalArgumentException("Le montant ajouté doit être positif."))
        }

        val objectif = objectifRepository.getObjectifsFlow().first().find { it.id == id }
            ?: return@withContext Result.failure(Exception("Objectif non trouvé."))

        val nouveauMontant = (objectif.montantActuel + montantAjoute).coerceAtMost(objectif.montantCible)
        objectifRepository.updateObjectif(objectif.copy(montantActuel = nouveauMontant))
        Result.success(Unit)
    }

    suspend fun deleteObjectif(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        objectifRepository.deleteObjectif(id)
        Result.success(Unit)
    }
}
