package com.example.limargent.Data.Preference

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.Preference.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceProvider @Inject constructor(
    private val userProvider: UserProvider
) : PreferenceRepository {

    override suspend fun getTheme(): String {
        val user = userProvider.getCurrentUser()
        return user?.preference?.firstOrNull { it.startsWith("theme:") }?.substringAfter("theme:") ?: "system"
    }

    override suspend fun setTheme(theme: String) {
        val user = userProvider.getCurrentUser()
        user?.let {
            val newPreferences = it.preference.filter { pref -> !pref.startsWith("theme:") }.toMutableList()
            newPreferences.add("theme:$theme")
            val updatedUser = it.copy(preference = newPreferences)
            userProvider.updateUser(updatedUser)
        }
    }

    override suspend fun getSeuilBudget(): Double {
        val user = userProvider.getCurrentUser()
        return user?.preference?.firstOrNull { it.startsWith("seuil:") }?.substringAfter("seuil:")?.toDoubleOrNull() ?: 0.0
    }

    override suspend fun setSeuilBudget(seuil: Double) {
        val user = userProvider.getCurrentUser()
        user?.let {
            val newPreferences = it.preference.filter { pref -> !pref.startsWith("seuil:") }.toMutableList()
            newPreferences.add("seuil:$seuil")
            val updatedUser = it.copy(preference = newPreferences)
            userProvider.updateUser(updatedUser)
        }
    }

    override suspend fun isNotificationActive(): Boolean {
        val user = userProvider.getCurrentUser()
        return user?.preference?.firstOrNull { it.startsWith("notif:") }?.substringAfter("notif:")?.toBoolean() ?: false
    }

    override suspend fun setNotificationActive(active: Boolean) {
        val user = userProvider.getCurrentUser()
        user?.let {
            val newPreferences = it.preference.filter { pref -> !pref.startsWith("notif:") }.toMutableList()
            newPreferences.add("notif:$active")
            val updatedUser = it.copy(preference = newPreferences)
            userProvider.updateUser(updatedUser)
        }
    }
}