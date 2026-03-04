package com.example.limargent.Domain.Preference

interface PreferenceRepository {
    suspend fun getTheme(): String
    suspend fun setTheme(theme: String)
}