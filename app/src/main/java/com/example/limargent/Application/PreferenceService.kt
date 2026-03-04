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

}