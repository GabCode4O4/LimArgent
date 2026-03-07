package com.example.limargent.domain.model

data class User(
    val identifiant: String="",
    val categories: MutableList<String> = mutableListOf(),
    val preference: MutableMap<String, Any> = mutableMapOf()
)