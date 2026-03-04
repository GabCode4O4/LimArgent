package com.example.limargent.Domain

import java.math.BigDecimal
import java.time.LocalDate

enum class MovType{
    entree,sortie
}

data class MoneyMovement(
    val titre: String = "",
    val montant: BigDecimal = BigDecimal.ZERO, // On utilise BigDecimal car la précision est importante dès qu'on touche au financier
    val categorie: String = "",
    val date: LocalDate = LocalDate.now(), // LocalDate permet de faire du tri et du parsing plus facilement et garantit le format
    val type: MovType = MovType.entree,
    val id: String? = null
)