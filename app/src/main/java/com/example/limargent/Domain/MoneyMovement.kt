package com.example.limargent.Domain

import java.math.BigDecimal
import java.time.LocalDate

enum class MovType{
    entree,sortie
}

data class MoneyMovement(
    // On définit l'objet comme immuable en utilisant des val pour éviter les effets de bord, on peut donc laisser les attributs en public
    val titre: String,
    val montant: BigDecimal, // On utilise BigDecimal car la précision est importante dès qu'on touche au financier
    val categorie: String,
    val date: LocalDate, // LocalDate permet de faire du tri et du parsing plus facilement et garantit le format
    val type: MovType,
    val id: String? = null
)