package com.example.limargent.Application

import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryService @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend fun getCategories(): List<String> {
        return withContext(Dispatchers.IO) {
            categoryRepository.getCategories()
        }
    }

    suspend fun addCategorie(nom: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.addCategorie(nom)
        }
    }

    suspend fun supprimer(nom: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.supprimer(nom)
        }
    }
    fun getCategoriesFlow(): Flow<List<String>> {
        return categoryRepository.getCategoriesFlow()
    }
}