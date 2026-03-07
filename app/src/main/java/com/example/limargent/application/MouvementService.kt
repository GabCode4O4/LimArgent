package com.example.limargent.application

import com.example.limargent.domain.model.Frequence
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.mouvements.MouvementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class MouvementService @Inject constructor(
    private val mouvementRepository: MouvementRepository,
) {

    fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return mouvementRepository.getMouvementsFlow()
    }

    suspend fun saveMouvement(mouvement: MoneyMovement): Result<Unit> = withContext(Dispatchers.IO) {
        if (mouvement.titre.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Le titre ne peut pas être vide."))
        }
        if (mouvement.montant <= BigDecimal.ZERO) {
            return@withContext Result.failure(IllegalArgumentException("Le montant doit être supérieur à zéro."))
        }
        if (mouvement.categorie.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez sélectionner une catégorie."))
        }

        val finalMouvement = computeRecurrence(mouvement)

        val result = if (finalMouvement.id == null) {
            mouvementRepository.addMouvement(finalMouvement)
        } else {
            mouvementRepository.updateMouvement(finalMouvement)
        }

        result
    }

    suspend fun deleteMouvement(mouvement: MoneyMovement): Result<Unit> = withContext(Dispatchers.IO) {
        val result = mouvementRepository.deleteMouvement(mouvement)
        result
    }

    suspend fun stopRecurrence(mouvement: MoneyMovement): Result<Unit> = withContext(Dispatchers.IO) {
        if (!mouvement.isRecurrent) {
            return@withContext Result.success(Unit)
        }
        val updatedMouvement = mouvement.copy(
            isRecurrent = false,
            frequence = Frequence.AUCUNE,
            nextDate = null
        )
        mouvementRepository.updateMouvement(updatedMouvement)
    }

    suspend fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate): Result<Unit> = withContext(Dispatchers.IO) {
        if (!mouvement.isRecurrent) {
            return@withContext Result.failure(IllegalArgumentException("Ce mouvement n'est pas récurrent."))
        }
        if (newDate.isBefore(LocalDate.now())) {
            return@withContext Result.failure(IllegalArgumentException("La date de prochaine exécution ne peut pas être dans le passé."))
        }
        val updatedMouvement = mouvement.copy(nextDate = newDate)
        mouvementRepository.updateMouvement(updatedMouvement)
    }

    private fun computeRecurrence(mouvement: MoneyMovement): MoneyMovement {
        if (!mouvement.isRecurrent || mouvement.frequence == Frequence.AUCUNE) {
            return mouvement.copy(isRecurrent = false, frequence = Frequence.AUCUNE, nextDate = null)
        }

        if (mouvement.nextDate != null && mouvement.nextDate.isAfter(mouvement.date)) {
            return mouvement
        }

        val nextDate = mouvement.date.ajouterFrequence(mouvement.frequence)
        return mouvement.copy(nextDate = nextDate)
    }



    suspend fun verifierMouvRec() = withContext(Dispatchers.IO) {
        val aujourdHui = LocalDate.now()
        val mouvements = mouvementRepository.getMouvementsFlow().first()
        val mouvementsATraiter = mouvements.filter {
            it.isRecurrent && it.nextDate != null && !it.nextDate.isAfter(aujourdHui)
        }

        for (mouvement in mouvementsATraiter) {
            var dateExecution = mouvement.nextDate!!

            while (!dateExecution.isAfter(aujourdHui)) {

                val nouvelleTransaction = mouvement.copy(
                    id = null,
                    date = dateExecution,
                    isRecurrent = false,
                    frequence = Frequence.AUCUNE,
                    nextDate = null
                )
                mouvementRepository.addMouvement(nouvelleTransaction)
                dateExecution = mouvement.date.ajouterFrequence(mouvement.frequence)
            }
            val parentMisAJour = mouvement.copy(nextDate = dateExecution)
            mouvementRepository.updateMouvement(parentMisAJour)
        }
    }
}


fun LocalDate.ajouterFrequence(frequence: Frequence): LocalDate {
    return when (frequence) {
        Frequence.QUOTIDIEN -> this.plusDays(1)
        Frequence.HEBDOMADAIRE -> this.plusWeeks(1)
        Frequence.MENSUEL -> this.plusMonths(1)
        Frequence.ANNUEL -> this.plusYears(1)
        Frequence.AUCUNE -> this
    }
}