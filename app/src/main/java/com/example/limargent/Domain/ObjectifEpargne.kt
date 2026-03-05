package com.example.limargent.Domain

import java.time.LocalDate
import java.util.UUID

data class ObjectifEpargne(
    val id: String = UUID.randomUUID().toString(),
    val nom: String,
    val montantCible: Double,
    var montantActuel: Double = 0.0,
    val dateEcheance: LocalDate? = null
) {
    val progression: Float
        get() = if (montantCible > 0) (montantActuel / montantCible).toFloat().coerceIn(0f, 1f) else 0f
        
    val estAtteint: Boolean
        get() = montantActuel >= montantCible
}