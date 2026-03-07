package com.example.limargent.domain.dataClasse

data class User(
    val identifiant: String="",
    var mouvements: MutableList<MoneyMovement> = mutableListOf(),
    val categories: MutableList<String> = mutableListOf(),
    val preference: MutableList<String> = mutableListOf()
)