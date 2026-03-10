package com.example.limargent.application

import com.example.limargent.domain.model.ObjectifEpargne
import com.example.limargent.domain.objectif.ObjectifRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class ObjectifService @Inject constructor(
    private val objectifRepository: ObjectifRepository
) {
//Les méthodes flow comme celle ci nous permettent de récup en temps rééls les données pour que ce soit tjrs à jour
    fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> {
        return objectifRepository.getObjectifsFlow()
    }

    suspend fun saveObjectif(objectif: ObjectifEpargne): Result<Unit> {
        if (objectif.nom == "" || objectif.nom.trim() == "") {
            return Result.failure(IllegalArgumentException("Le nom de l'objectif ne peut pas être vide."))
        }
        if (objectif.montantCible.compareTo(BigDecimal(0)) <= 0) {
            return Result.failure(IllegalArgumentException("Le montant cible doit être positif."))
        }
        if (objectif.dateEcheance != null) {
            if (objectif.dateEcheance.isBefore(LocalDate.now()) == true) {
                return Result.failure(IllegalArgumentException("La date d'échéance ne peut pas être dans le passé."))
            }
        }
        var finalObjectif = objectif
        if (objectif.id == "" || objectif.id.trim() == "") {
            finalObjectif = objectif.copy(id = UUID.randomUUID().toString())
        }

        objectifRepository.updateObjectif(finalObjectif)
        return Result.success(Unit)
    }

    suspend fun addProgres(id: String, montantAjoute: BigDecimal): Result<Unit> {
        if (montantAjoute.compareTo(BigDecimal(0)) <= 0) {
            return Result.failure(IllegalArgumentException("Le montant ajouté doit être positif."))
        }

        val listeObjectifs = objectifRepository.getObjectifsFlow().first()
        var objectifTrouve: ObjectifEpargne? = null
        for (i in 0 until listeObjectifs.size) {
            if (listeObjectifs[i].id == id) {
                objectifTrouve = listeObjectifs[i]
            }
        }
        if (objectifTrouve == null) {
            return Result.failure(Exception("Objectif non trouvé."))
        }
        var nouveauMontant = objectifTrouve.montantActuel + montantAjoute

        if (nouveauMontant.compareTo(objectifTrouve.montantCible) > 0) {
            nouveauMontant = objectifTrouve.montantCible
        }

        val objMisAJour = objectifTrouve.copy(montantActuel = nouveauMontant)
        objectifRepository.updateObjectif(objMisAJour)

        return Result.success(Unit)
    }
    suspend fun deleteObjectif(id: String): Result<Unit> {
        objectifRepository.deleteObjectif(id)
        return Result.success(Unit)
    }
}