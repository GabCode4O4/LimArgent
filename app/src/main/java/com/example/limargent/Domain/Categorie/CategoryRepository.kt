package com.example.limargent.Domain.Categorie

interface CategoryRepository {
    fun getCategories(): List<String>
    suspend fun addCat(nom : String)
    suspend fun delCat(nom : String)
}