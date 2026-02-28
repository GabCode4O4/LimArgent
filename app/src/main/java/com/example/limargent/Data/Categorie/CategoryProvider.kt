package com.example.limargent.Data.Categorie

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import java.io.File
import javax.inject.Inject

class CategoryProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fileName = "categories.json"
    private val categories = mutableListOf<String>()

    init {
        loadCategories()
    }

    fun getCategories(): List<String> {
        return categories.toList()
    }

    fun addCategorie(nom: String) {
        if (!categories.contains(nom)) {
            categories.add(nom)
            sauvegarder()
        }
    }

    fun supprimer(nom: String) {
        if (categories.remove(nom)) {
            sauvegarder()
        }
    }

    private fun loadCategories() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val jsonArray = JSONArray(jsonString)
                categories.clear()
                for (i in 0 until jsonArray.length()) {
                    categories.add(jsonArray.getString(i))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                defautCategories()
            }
        } else {
            defautCategories()
        }
    }

    private fun defautCategories() {
        categories.clear()
        categories.addAll(listOf(
            "Alimentation", "Logement", "Transports", "Santé", "Loisirs", "Épargne", "Salaire", "Autre"
        ))
        sauvegarder()
    }

    fun sauvegarder() {
        try {
            val jsonArray = JSONArray(categories)
            val file = File(context.filesDir, fileName)
            file.writeText(jsonArray.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}