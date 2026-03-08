package com.example.limargent.domain.model

import java.math.BigDecimal
import java.time.LocalDate
enum class MovType {
    entree,
    sortie
}
enum class Frequence(val libelle: String) {
    AUCUNE("Aucune"),
    QUOTIDIEN("Quotidien"),
    HEBDOMADAIRE("Hebdomadaire"),
    MENSUEL("Mensuel"),
    ANNUEL("Annuel")
}

data class MoneyMovement(
    val titre: String = "",
    val montant: BigDecimal = BigDecimal.ZERO, 
    
    val categorie: String = "",
    val date: LocalDate = LocalDate.now(), 
    
    val type: MovType = MovType.entree,
    val id: String? = null,
    val isRecurrent: Boolean = false,
    val frequence: Frequence = Frequence.AUCUNE,
    val nextDate: LocalDate? = null 
)
