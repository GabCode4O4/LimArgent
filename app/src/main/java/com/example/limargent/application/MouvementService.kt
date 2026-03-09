package com.example.limargent.application

import com.example.limargent.domain.model.Frequence
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.mouvements.MouvementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class MouvementService @Inject constructor(
    private val mouvementRepository: MouvementRepository,
) {
    fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return mouvementRepository.getMouvementsFlow()
    }
    suspend fun saveMouvement(mouvement: MoneyMovement): Result<Unit> {
        if (mouvement.titre == "" || mouvement.titre.trim() == "") {
            return Result.failure(IllegalArgumentException("Le titre ne peut pas être vide."))
        }
        if (mouvement.montant.compareTo(BigDecimal(0)) <= 0) {
            return Result.failure(IllegalArgumentException("Le montant doit être supérieur à zéro."))
        }
        if (mouvement.categorie == "" || mouvement.categorie.trim() == "") {
            return Result.failure(IllegalArgumentException("Veuillez sélectionner une catégorie."))
        }

        val finalMouvement = computeRecurrence(mouvement)
        if (finalMouvement.id == null) {
            return mouvementRepository.addMouvement(finalMouvement)
        } else {
            return mouvementRepository.updateMouvement(finalMouvement)
        }
    }
    suspend fun deleteMouvement(mouvement: MoneyMovement): Result<Unit> {
        return mouvementRepository.deleteMouvement(mouvement)
    }
    suspend fun stopRecurrence(mouvement: MoneyMovement): Result<Unit> {
        if (mouvement.isRecurrent == false) {
            return Result.success(Unit)
        }

        val updatedMouvement = mouvement.copy(
            isRecurrent = false,
            frequence = Frequence.AUCUNE,
            nextDate = null
        )
        return mouvementRepository.updateMouvement(updatedMouvement)
    }
    suspend fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate): Result<Unit> {
        if (mouvement.isRecurrent == false) {
            return Result.failure(IllegalArgumentException("Ce mouvement n'est pas récurrent."))
        }
        if (newDate.isBefore(LocalDate.now()) == true) {
            return Result.failure(IllegalArgumentException("La date de prochaine exécution ne peut pas être dans le passé."))
        }
        val updatedMouvement = mouvement.copy(nextDate = newDate)
        return mouvementRepository.updateMouvement(updatedMouvement)
    }
    private fun computeRecurrence(mouvement: MoneyMovement): MoneyMovement {
        if (mouvement.isRecurrent == false || mouvement.frequence == Frequence.AUCUNE) {
            return mouvement.copy(isRecurrent = false, frequence = Frequence.AUCUNE, nextDate = null)
        }

        if (mouvement.nextDate != null && mouvement.nextDate.isAfter(mouvement.date) == true) {
            return mouvement
        }
        val nextDate = ajouterFrequence(mouvement.date, mouvement.frequence)
        return mouvement.copy(nextDate = nextDate)
    }

    suspend fun verifierMouvRec() {
        val aujourdHui = LocalDate.now()
        val mouvements = mouvementRepository.getMouvementsFlow().first()
        val mouvementsATraiter = mutableListOf<MoneyMovement>()

        for (i in 0 until mouvements.size) {
            val mouv = mouvements[i]
            if (mouv.isRecurrent == true && mouv.nextDate != null) {
                if (mouv.nextDate.isAfter(aujourdHui) == false) {
                    mouvementsATraiter.add(mouv)
                }
            }
        }

        // On ne traite que les mouvements dont la date d'exécution est passée et qu'il faut donc actualiser

        for (i in 0 until mouvementsATraiter.size) {
            val mouvement = mouvementsATraiter[i]
            var dateExecution = mouvement.nextDate!!

            while (dateExecution.isAfter(aujourdHui) == false) {
                val nouvelleTransaction = mouvement.copy(
                    id = null,
                    date = dateExecution,
                    isRecurrent = false,
                    frequence = Frequence.AUCUNE,
                    nextDate = null
                )
                mouvementRepository.addMouvement(nouvelleTransaction)

                dateExecution = ajouterFrequence(dateExecution, mouvement.frequence)
            }

            val parentMisAJour = mouvement.copy(nextDate = dateExecution)
            mouvementRepository.updateMouvement(parentMisAJour)
        }
    }
    private fun ajouterFrequence(date: LocalDate, frequence: Frequence): LocalDate {
        if (frequence == Frequence.QUOTIDIEN) {
            return date.plusDays(1)
        } else if (frequence == Frequence.HEBDOMADAIRE) {
            return date.plusWeeks(1)
        } else if (frequence == Frequence.MENSUEL) {
            return date.plusMonths(1)
        } else if (frequence == Frequence.ANNUEL) {
            return date.plusYears(1)
        } else {
            return date
        }
    }
}