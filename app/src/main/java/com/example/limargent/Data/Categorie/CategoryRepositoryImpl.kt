package com.example.limargent.Data.Categorie

import com.example.limargent.Domain.Categorie.CategoryRepository

class CategoryRepositoryImpl(
    private val categoryProvider: CategoryProvider
) : CategoryRepository {
    override fun getCategories(): List<String> {
        return categoryProvider.getCategories()
    }
    override suspend fun addCat(nom: String) {
        categoryProvider.addCategory(nom)
    }
    override suspend fun delCat(nom: String) {
        categoryProvider.removeCategory(nom)
    }
    override suspend fun update() {
        // TODO: Implement update logic
    }

    override suspend fun delete() {
        // TODO: Implement delete logic
    }

    override suspend fun save() {
        // TODO: Implement save logic
    }
}