package com.example.limargent.domain.categorie

interface CategoryRepository {
    suspend fun getCategories(): List<String>
    suspend fun addCategorie(nom : String)
    suspend fun supprimer(nom : String)
    fun getCategoriesFlow(): kotlinx.coroutines.flow.Flow<List<String>>
}