package com.example.limargent.Data.Categorie

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.Categorie.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryProvider @Inject constructor(
    private val userProvider: UserProvider
) : CategoryRepository {

    override suspend fun getCategories(): List<String> {
        val user = userProvider.getCurrentUser()
        return user?.categories?.toList() ?: emptyList()
    }

    override suspend fun addCategorie(nom: String) {
        userProvider.addCategoryToCurrentUser(nom)
    }

    override suspend fun supprimer(nom: String) {
        userProvider.removeCategoryFromCurrentUser(nom)
    }
    override fun getCategoriesFlow(): Flow<List<String>> {
        return userProvider.getCategoriesFlow()
    }
}