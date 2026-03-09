package com.example.limargent.presentation.util

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val date = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.toDateFormat(): String {
    return this.format(date)
}

// permet d'avoir un string prêt à afficher pour des montants
fun BigDecimal.toArgent(): String {
    return "${"%.2f".format(this)} €"
}

fun Float.toArgent(): String {
    return "${"%.2f".format(this)} €"
}

fun Double.toArgent(): String {
    return "${"%.2f".format(this)} €"
}