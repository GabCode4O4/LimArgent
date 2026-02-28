package com.example.limargent.Application

import com.example.limargent.Data.Categorie.CategoryProvider
import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryService @Inject constructor(
    private val categoryProvider: CategoryProvider
) : CategoryRepository {

    override fun getCategories(): List<String> {
        return categoryProvider.getCategories()
    }

    override suspend fun addCategorie(nom: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.addCategorie(nom)
        }
    }

    override suspend fun supprimerCat(nom: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.supprimer(nom)
        }
    }

    override suspend fun update() {
        // TODO: Implement update logic
    }

    override suspend fun supprimer() {
        // TODO: Implement delete logic
    }

    override suspend fun sauvegarder() {
        withContext(Dispatchers.IO) {
            categoryProvider.sauvegarder()
        }
    }
}