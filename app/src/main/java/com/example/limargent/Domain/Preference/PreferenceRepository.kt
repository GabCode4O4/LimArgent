package com.example.limargent.Domain.Preference

interface PreferenceRepository {
    fun getTheme(): String
    fun setTheme(theme: String)
}