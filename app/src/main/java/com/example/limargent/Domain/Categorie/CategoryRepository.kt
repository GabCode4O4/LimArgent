package com.example.limargent.Domain.Categorie

interface CategoryRepository {
    fun getCategories(): List<String>
    suspend fun addCategorie(nom : String)
    suspend fun supprimerCat(nom : String)

    suspend fun update()
    suspend fun supprimer()
    suspend fun sauvegarder()
}