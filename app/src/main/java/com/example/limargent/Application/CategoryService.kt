package com.example.limargent.Application

import com.example.limargent.Data.Categorie.CategoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CategoryService(private val categoryProvider: CategoryProvider) {

    fun getCategories(): List<String> {
        return categoryProvider.getCategories()
    }

    suspend fun addCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.addCategory(name)
        }
    }

    suspend fun removeCategory(name: String) {
        withContext(Dispatchers.IO) {
            categoryProvider.removeCategory(name)
        }
    }
}

