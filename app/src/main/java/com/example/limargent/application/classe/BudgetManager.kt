package com.example.limargent.application.classe

import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.model.MovType
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetManager @Inject constructor() {
    fun getBudget(mouvements: List<MoneyMovement>): BigDecimal {
        //Calcule le montant total des mouvements
        var total = BigDecimal(0)
        for (i in 0 until mouvements.size) {
            val m = mouvements[i]
            if (m.type == MovType.entree) {
                total = total + m.montant
            } else {
                total = total - m.montant
            }
        }
        return total
    }
    fun getBudgetCategorie(mouvements: List<MoneyMovement>, type: MovType): Map<String, Float> {
        //Calcule le montant total des mouvements par catégorie
        val categories = mutableMapOf<String, Float>()

        for (mouv in mouvements) {
            if (mouv.type == type) {
                if (categories.containsKey(mouv.categorie)) {
                    val ancienTotal = categories[mouv.categorie]!!
                    categories[mouv.categorie] = ancienTotal + mouv.montant.toFloat()
                } else {
                    categories[mouv.categorie] = mouv.montant.toFloat()
                }
            }
        }
        return categories.toList().sortedByDescending { it.second }.toMap()
    }

    fun getSousCategories(mouvements: List<MoneyMovement>, categorie: String): Map<String, Float> {
        //Calcule le montant total des mouvements par sous-catégorie
        val sousCat = mutableMapOf<String, Float>()
        for (mouv in mouvements) {
            if (mouv.categorie == categorie) {
                if (sousCat[mouv.titre] == null) {
                    sousCat[mouv.titre] = mouv.montant.toFloat()
                } else {
                    sousCat[mouv.titre] = sousCat[mouv.titre]!! + mouv.montant.toFloat()
                }
            }
        }
        return sousCat.toList().sortedByDescending { it.second }.toMap()
    }
}
