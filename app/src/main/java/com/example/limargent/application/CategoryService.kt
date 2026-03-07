package com.example.limargent.application

import com.example.limargent.domain.categorie.CategoryRepository
import com.example.limargent.domain.mouvements.MouvementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryService @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val mouvementRepository: MouvementRepository
) {

    fun getCategoriesFlow(): Flow<List<String>> {
        return categoryRepository.getCategoriesFlow()
    }

    suspend fun addCategorie(nom: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (nom.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Le nom de la catégorie ne peut pas être vide."))
        }
        val categoriesActuelles = categoryRepository.getCategoriesFlow().first()
        if (categoriesActuelles.any { it.equals(nom, ignoreCase = true) }) {
            return@withContext Result.failure(IllegalArgumentException("Cette catégorie existe déjà."))
        }
        categoryRepository.addCategorie(nom)
        Result.success(Unit)
    }

    suspend fun supprimer(nom: String): Result<Unit> = withContext(Dispatchers.IO) {
        val mouvements = mouvementRepository.getMouvementsFlow().first()
        if (mouvements.any { it.categorie.equals(nom, ignoreCase = true) }) {
            return@withContext Result.failure(Exception("Impossible de supprimer une catégorie utilisée par un ou plusieurs mouvements."))
        }
        categoryRepository.supprimer(nom)
        Result.success(Unit)
    }
}
