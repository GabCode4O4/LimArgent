package com.example.limargent.Application

import com.example.limargent.Data.Preference.PreferenceProvider
import javax.inject.Inject

class PreferenceService @Inject constructor(private val preferenceProvider: PreferenceProvider) {
    fun getTheme(): String {
        return preferenceProvider.getTheme()
    }

    fun setTheme(theme: String) {
        preferenceProvider.setTheme(theme)
    }

}