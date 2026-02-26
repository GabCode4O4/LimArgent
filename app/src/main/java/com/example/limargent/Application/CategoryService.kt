package com.example.limargent.Application

import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryService(private val categoryRepository: CategoryRepository) {

    fun getCategories(): List<String> {
        return categoryRepository.getCategories()
    }

    suspend fun addCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.addCat(name)
        }
    }

    suspend fun removeCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryRepository.delCat(name)
        }
    }
}