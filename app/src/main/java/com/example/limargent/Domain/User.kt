package com.example.limargent.Domain

import android.preference.Preference

data class User(
    val identifiant: String,
    val motDePasse: String,
    val mouvements: MutableList<MoneyMovement> = mutableListOf(),
    val categories: MutableList<String> = mutableListOf(),
    val preference: MutableList<String> = mutableListOf()
)