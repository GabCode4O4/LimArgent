package com.example.limargent.Application

import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryService @Inject constructor(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<String> {
        return categoryRepository.getCategories()
    }

    suspend fun addCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.addCategorie(name)
        }
    }

    suspend fun removeCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.supprimerCat(name)
        }
    }
}