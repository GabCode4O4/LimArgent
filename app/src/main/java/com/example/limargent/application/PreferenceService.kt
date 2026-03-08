package com.example.limargent.application

import com.example.limargent.domain.preferences.PreferenceRepository
import javax.inject.Inject

class PreferenceService @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {

    suspend fun getTheme(): String {
        val themeActuel = preferenceRepository.getTheme()
        return themeActuel
    }

    suspend fun setTheme(theme: String): Result<Unit> {
        preferenceRepository.setTheme(theme)
        return Result.success(Unit)
    }

    suspend fun getSeuilBudget(): Double {
        val seuil = preferenceRepository.getSeuilBudget()
        return seuil
    }

    suspend fun setSeuilBudget(seuil: Double): Result<Unit> {
        if (seuil < 0.0 == true) {
            return Result.failure(IllegalArgumentException("Le seuil ne peut pas être négatif."))
        }

        preferenceRepository.setSeuilBudget(seuil)
        return Result.success(Unit)
    }

    suspend fun isNotificationActive(): Boolean {
        val isActive = preferenceRepository.isNotificationActive()
        if (isActive == true) {
            return true
        } else {
            return false
        }
    }

    suspend fun setNotificationActive(active: Boolean): Result<Unit> {
        preferenceRepository.setNotificationActive(active)
        return Result.success(Unit)
    }
}