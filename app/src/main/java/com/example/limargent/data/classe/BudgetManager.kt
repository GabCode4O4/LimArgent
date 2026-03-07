package com.example.limargent.data.classe

import com.example.limargent.domain.dataClasse.MoneyMovement
import com.example.limargent.domain.dataClasse.MovType
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetManager @Inject constructor() {

    // 1. Calcul du solde total
    fun getBudget(mouvements: List<MoneyMovement>): BigDecimal {
        // L'utilisation de 'fold' est plus optimisée et propre en Kotlin qu'une boucle 'for'
        return mouvements.fold(BigDecimal.ZERO) { acc, mouv ->
            if (mouv.type == MovType.entree) acc.add(mouv.montant)
            else acc.subtract(mouv.montant)
        }
    }

    fun getBudgetCategorie(mouvements: List<MoneyMovement>, type: MovType): Map<String, Float> {
        val categories = mutableMapOf<String, Float>()
        for (mouv in mouvements) {
            if (mouv.type == type) {
                val totalActuel = categories.getOrDefault(mouv.categorie, 0f)
                categories[mouv.categorie] = totalActuel + mouv.montant.toFloat()
            }
        }

        return categories.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }

    fun getSousCategories(mouvements: List<MoneyMovement>, categorie: String): Map<String, Float> {
        val chartData = mutableMapOf<String, Float>()
        for (mouv in mouvements) {
            if (mouv.categorie == categorie) {
                val actuel = chartData.getOrDefault(mouv.categorie, 0f)
                chartData[mouv.titre] = actuel + mouv.montant.toFloat()
            }
        }
        return chartData.entries.sortedByDescending { it.value }.associate { it.toPair() }
    }
}