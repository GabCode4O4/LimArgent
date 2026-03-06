package com.example.limargent.Data.Classe

import com.example.limargent.Domain.DataClasse.MoneyMovement
import com.example.limargent.Domain.DataClasse.MovType
import java.math.BigDecimal

class BudgetManager{
    fun getBudget(mouvements: List<MoneyMovement>) : BigDecimal {
        var total = BigDecimal.ZERO
        for (mouv in mouvements){
            if(mouv.type == MovType.entree){
                total = total+(mouv.montant)}
            else{
                total = total-(mouv.montant)
            }

        }
        return total
    }

    fun getBudgetCategorie(mouvements: List<MoneyMovement>):Map<String, BigDecimal>{
        val categories = HashMap<String, BigDecimal>()

        for (mouv in mouvements) {
            if (mouv.montant < BigDecimal.ZERO) {
                val total = categories.getOrDefault(mouv.categorie, BigDecimal.ZERO)
                categories[mouv.categorie] = total.add(mouv.montant)
            }
        }
        return categories
    }

}