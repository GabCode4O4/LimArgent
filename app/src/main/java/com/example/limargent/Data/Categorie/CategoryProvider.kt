package com.example.limargent.Data.Categorie


class CategoryProvider {
    private val categories = mutableListOf(
        "Alimentation", "Logement", "Transports", "Santé", "Loisirs", "Épargne", "Salaire", "Autre"
    )

    fun getCategories(): List<String> {
        return categories.toList()
    }

    fun addCategory(nom: String) {
        if (!categories.contains(nom)) {
            categories.add(nom)
        }
    }

    fun removeCategory(nom: String) {
        categories.remove(nom)
    }
}