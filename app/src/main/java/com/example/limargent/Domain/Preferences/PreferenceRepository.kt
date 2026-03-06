package com.example.limargent.Domain.Preferences

interface PreferenceRepository {
    suspend fun getTheme(): String
    suspend fun setTheme(theme: String)
    suspend fun getSeuilBudget(): Double
    suspend fun setSeuilBudget(seuil: Double)
    suspend fun isNotificationActive(): Boolean
    suspend fun setNotificationActive(active: Boolean)
}
