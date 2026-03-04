package com.example.limargent.Application

import com.example.limargent.Data.Preference.PreferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PreferenceService @Inject constructor(private val preferenceProvider: PreferenceProvider) {
    suspend fun getTheme(): String {
        return withContext(Dispatchers.IO) {
            preferenceProvider.getTheme()
        }
    }

    suspend fun setTheme(theme: String) {
        withContext(Dispatchers.IO) {
            preferenceProvider.setTheme(theme)
        }
    }

}