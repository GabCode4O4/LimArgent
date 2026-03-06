package com.example.limargent.Application

import com.example.limargent.Domain.DataClasse.Frequence
import com.example.limargent.Domain.DataClasse.MoneyMovement
import com.example.limargent.Domain.DataClasse.MovType
import com.example.limargent.Domain.Mouvements.MovementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class MovementsService @Inject constructor(
    private val movementsRepository: MovementsRepository,
    private val preferenceService: PreferenceService
) {

    fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return movementsRepository.getMouvementsFlow()
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
            movementsRepository.addMouvement(finalMouvement)
        } else {
            movementsRepository.updateMouvement(finalMouvement)
        }

        if (result.isSuccess) {
            checkBudgetThreshold()
        }

        result
    }

    suspend fun deleteMouvement(mouvement: MoneyMovement): Result<Unit> = withContext(Dispatchers.IO) {
        val result = movementsRepository.deleteMouvement(mouvement)
        if (result.isSuccess) {
            checkBudgetThreshold()
        }
        result
    }

    suspend fun stopRecurrence(mouvement: MoneyMovement): Result<Unit> = withContext(Dispatchers.IO) {
        if (!mouvement.isRecurrent) {
            return@withContext Result.success(Unit) // Rien à faire
        }
        val updatedMouvement = mouvement.copy(
            isRecurrent = false,
            frequence = Frequence.AUCUNE,
            nextExecutionDate = null
        )
        movementsRepository.updateMouvement(updatedMouvement)
    }

    suspend fun updateRecurrenceDate(mouvement: MoneyMovement, newDate: LocalDate): Result<Unit> = withContext(Dispatchers.IO) {
        if (!mouvement.isRecurrent) {
            return@withContext Result.failure(IllegalArgumentException("Ce mouvement n'est pas récurrent."))
        }
        if (newDate.isBefore(LocalDate.now())) {
            return@withContext Result.failure(IllegalArgumentException("La date de prochaine exécution ne peut pas être dans le passé."))
        }
        val updatedMouvement = mouvement.copy(nextExecutionDate = newDate)
        movementsRepository.updateMouvement(updatedMouvement)
    }

    private fun computeRecurrence(mouvement: MoneyMovement): MoneyMovement {
        if (!mouvement.isRecurrent || mouvement.frequence == Frequence.AUCUNE) {
            return mouvement.copy(isRecurrent = false, frequence = Frequence.AUCUNE, nextExecutionDate = null)
        }

        if (mouvement.nextExecutionDate != null && mouvement.nextExecutionDate.isAfter(mouvement.date)) {
            return mouvement
        }

        val nextDate = when (mouvement.frequence) {
            Frequence.QUOTIDIEN -> mouvement.date.plusDays(1)
            Frequence.HEBDOMADAIRE -> mouvement.date.plusWeeks(1)
            Frequence.MENSUEL -> mouvement.date.plusMonths(1)
            Frequence.ANNUEL -> mouvement.date.plusYears(1)
            Frequence.AUCUNE -> null
        }
        return mouvement.copy(nextExecutionDate = nextDate)
    }

    private suspend fun checkBudgetThreshold() {
        if (!preferenceService.isNotificationActive()) return

        val seuil = preferenceService.getSeuilBudget()
        val budgetActuel = calculateCurrentBudget(movementsRepository.getMouvementsFlow().first())

        if (budgetActuel < seuil.toBigDecimal()) {
            println("ALERTE : Le budget est sous le seuil de $seuil !")
        }
    }

    private fun calculateCurrentBudget(mouvements: List<MoneyMovement>): BigDecimal {
        return mouvements.fold(BigDecimal.ZERO) { acc, mouvement ->
            when (mouvement.type) {
                MovType.entree -> acc.add(mouvement.montant)
                MovType.sortie -> acc.subtract(mouvement.montant)
            }
        }
    }
}
