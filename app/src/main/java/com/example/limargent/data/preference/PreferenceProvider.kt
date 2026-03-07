package com.example.limargent.data.preference

import com.example.limargent.domain.preferences.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceProvider @Inject constructor() : PreferenceRepository {

    private var currentTheme = "system"
    private var currentSeuil = 100.0
    private var notificationsActive = true

    override suspend fun getTheme(): String {
        return currentTheme
    }

    override suspend fun setTheme(theme: String) {
        currentTheme = theme
    }

    override suspend fun getSeuilBudget(): Double {
        return currentSeuil
    }

    override suspend fun setSeuilBudget(seuil: Double) {
        currentSeuil = seuil
    }

    override suspend fun isNotificationActive(): Boolean {
        return notificationsActive
    }

    override suspend fun setNotificationActive(active: Boolean) {
        notificationsActive = active
    }
}