package com.example.limargent.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class ObjectifEpargne(
    val id: String = UUID.randomUUID().toString(),
    
    val nom: String,
    
    val montantCible: BigDecimal,
    var montantActuel: BigDecimal = BigDecimal.ZERO,
    val dateEcheance: LocalDate? = null,
    val tauxInteret: BigDecimal = BigDecimal.ZERO
) {
    val progression: Float
        get() = if (montantCible > BigDecimal.ZERO) {
            (montantActuel.toFloat() / montantCible.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    val estAtteint: Boolean
        get() = montantActuel >= montantCible
    val estimation: BigDecimal
        get() = montantActuel.multiply(tauxInteret).divide(BigDecimal(100))
}
