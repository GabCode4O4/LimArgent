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
}