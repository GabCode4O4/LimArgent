package com.example.limargent.Domain.Categorie

interface CategoryRepository {
    suspend fun getCategories(): List<String>
    suspend fun addCategorie(nom : String)
    suspend fun supprimer(nom : String)
}