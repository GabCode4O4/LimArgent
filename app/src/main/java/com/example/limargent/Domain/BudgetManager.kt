package com.example.limargent.Domain
import java.math.BigDecimal
import java.util.ArrayList


class BudgetManager{
    private val mouvements = ArrayList<MoneyMovement>()

    fun getBudget() : BigDecimal{
        var total = BigDecimal.ZERO
        for (mouv in mouvements){
            total = total+(mouv.montant)

        }
        return total
    }

    fun getBudgetCategorie():Map<String, BigDecimal>{
        val categories = HashMap<String, BigDecimal>()

        for (mouv in mouvements) {
            if (mouv.montant < BigDecimal.ZERO) {
                val total = categories.getOrDefault(mouv.categorie, BigDecimal.ZERO)
                categories[mouv.categorie] = total.add(mouv.montant)
            }
        }
        return categories
    }

    fun addMov(mouv: MoneyMovement){
        mouvements.add(mouv)
    }

    fun getMov() : ArrayList<MoneyMovement>{
        return mouvements
    }
}