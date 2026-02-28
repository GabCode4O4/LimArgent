package com.example.limargent.Domain

data class User(
    val identifiant: String,
    val motDePasse: String,
    val mouvements: MutableList<MoneyMovement> = mutableListOf(),
    val categories: MutableList<String> = mutableListOf()
)