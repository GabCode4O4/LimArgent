package com.example.limargent.Application

import com.example.limargent.Domain.Preferences.PreferenceRepository // <-- BON IMPORT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferenceService @Inject constructor(private val preferenceRepository: PreferenceRepository) {

    suspend fun getTheme(): String = withContext(Dispatchers.IO) {
        preferenceRepository.getTheme()
    }

    suspend fun setTheme(theme: String): Result<Unit> = withContext(Dispatchers.IO) {
        preferenceRepository.setTheme(theme)
        Result.success(Unit)
    }

    suspend fun getSeuilBudget(): Double = withContext(Dispatchers.IO) {
        preferenceRepository.getSeuilBudget()
    }

    suspend fun setSeuilBudget(seuil: Double): Result<Unit> = withContext(Dispatchers.IO) {
        if (seuil < 0) {
            return@withContext Result.failure(IllegalArgumentException("Le seuil ne peut pas être négatif."))
        }
        preferenceRepository.setSeuilBudget(seuil)
        Result.success(Unit)
    }

    suspend fun isNotificationActive(): Boolean = withContext(Dispatchers.IO) {
        preferenceRepository.isNotificationActive()
    }

    suspend fun setNotificationActive(active: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        preferenceRepository.setNotificationActive(active)
        Result.success(Unit)
    }
}
