package com.example.limargent.Data.Categorie

import com.example.limargent.Application.CategoryService
import com.example.limargent.Domain.Categorie.CategoryRepository

class CategoryRepositoryImpl(
    private val categoryService: CategoryService
) : CategoryRepository {
    
    override fun getCategories(): List<String> {
        return categoryService.getCategories()
    }

    override suspend fun addCat(nom: String) {
        categoryService.addCategory(nom)
    }

    override suspend fun delCat(nom: String) {
        categoryService.removeCategory(nom)
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