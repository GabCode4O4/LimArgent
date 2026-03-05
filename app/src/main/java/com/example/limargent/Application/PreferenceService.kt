package com.example.limargent.Application


import com.example.limargent.Domain.Preference.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferenceService @Inject constructor(private val preferenceRepository: PreferenceRepository) {
    suspend fun getTheme(): String {
        return withContext(Dispatchers.IO) {
            preferenceRepository.getTheme()
        }
    }

    suspend fun setTheme(theme: String) {
        withContext(Dispatchers.IO) {
            preferenceRepository.setTheme(theme)
        }
    }

    suspend fun getSeuilBudget(): Double {
        return withContext(Dispatchers.IO) {
            preferenceRepository.getSeuilBudget()
        }
    }

    suspend fun setSeuilBudget(seuil: Double) {
        withContext(Dispatchers.IO) {
            preferenceRepository.setSeuilBudget(seuil)
        }
    }

    suspend fun isNotificationActive(): Boolean {
        return withContext(Dispatchers.IO) {
            preferenceRepository.isNotificationActive()
        }
    }

    suspend fun setNotificationActive(active: Boolean) {
        withContext(Dispatchers.IO) {
            preferenceRepository.setNotificationActive(active)
        }
    }

}