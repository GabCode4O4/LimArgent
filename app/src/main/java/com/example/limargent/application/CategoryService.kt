package com.example.limargent.application

import com.example.limargent.domain.categorie.CategoryRepository
import com.example.limargent.domain.mouvements.MouvementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CategoryService @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val mouvementRepository: MouvementRepository
) {
    fun getCategoriesFlow(): Flow<List<String>> {
        return categoryRepository.getCategoriesFlow()
    }
    suspend fun addCategorie(nom: String): Result<Unit> {
        if (nom == "" || nom.trim() == "") {
            return Result.failure(IllegalArgumentException("Le nom de la catégorie ne peut pas être vide."))
        }

        val categoriesActuelles = categoryRepository.getCategoriesFlow().first()

        var existeDeja = false
        for (i in 0 until categoriesActuelles.size) {
            if (categoriesActuelles[i].lowercase() == nom.lowercase()) {
                existeDeja = true
            }
        }
        if (existeDeja == true) {
            return Result.failure(IllegalArgumentException("Cette catégorie existe déjà."))
        }

        categoryRepository.addCategorie(nom)
        return Result.success(Unit)
    }
    suspend fun supprimer(nom: String): Result<Unit> {
        val mouvements = mouvementRepository.getMouvementsFlow().first()
        var estUtilise = false
        for (i in 0 until mouvements.size) {
            val mouv = mouvements[i]
            if (mouv.categorie.lowercase() == nom.lowercase()) {
                estUtilise = true
            }
        }
        if (estUtilise == true) {
            return Result.failure(Exception("Impossible de supprimer une catégorie utilisée par un ou plusieurs mouvements."))
        }
        categoryRepository.supprimer(nom)
        return Result.success(Unit)
    }
}