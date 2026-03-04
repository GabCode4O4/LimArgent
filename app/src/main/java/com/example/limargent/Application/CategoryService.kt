package com.example.limargent.Application

import com.example.limargent.Data.Categorie.CategoryProvider
import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryService @Inject constructor(
    private val categoryProvider: CategoryProvider
) : CategoryRepository {

    override suspend fun getCategories(): List<String> {
        return withContext(Dispatchers.IO) {
            categoryProvider.getCategories()
        }
    }

    override suspend fun addCategorie(nom: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.addCategorie(nom)
        }
    }

    override suspend fun supprimer(nom: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.supprimer(nom)
        }
    }
}