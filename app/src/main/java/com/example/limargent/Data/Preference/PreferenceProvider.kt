package com.example.limargent.Data.Preference

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.Preference.PreferenceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceProvider @Inject constructor(
    private val userProvider: UserProvider
) : PreferenceRepository {

    override fun getTheme(): String {
        val user = userProvider.getUser()
        return user?.preference?.firstOrNull { it.startsWith("theme:") }?.substringAfter("theme:") ?: "system"
    }

    override fun setTheme(theme: String) {
        val user = userProvider.getUser()
        user?.let {
            it.preference.removeAll { pref -> pref.startsWith("theme:") }
            it.preference.add("theme:$theme")
            userProvider.updateUser(it)
        }
    }
}