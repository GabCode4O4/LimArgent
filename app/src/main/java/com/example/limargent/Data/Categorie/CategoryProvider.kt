package com.example.limargent.Data.Categorie

import com.example.limargent.Data.SessionManager
import com.example.limargent.Data.User.UserProvider
import javax.inject.Inject

class CategoryProvider @Inject constructor(
    private val userProvider: UserProvider,
    private val sessionManager: SessionManager
) {

    private val currentUserId: String get() = sessionManager.UserIdentifiant.value ?: "default"

    fun getCategories(): List<String> {
        val user = userProvider.getUser(currentUserId)
        return user?.categories?.toList() ?: emptyList()
    }

    fun addCategorie(nom: String) {
        val user = userProvider.getUser(currentUserId)
        if (user != null && !user.categories.contains(nom)) {
            user.categories.add(nom)
            userProvider.saveUser(user)
        }
    }

    fun supprimer(nom: String) {
        val user = userProvider.getUser(currentUserId)
        if (user != null && user.categories.remove(nom)) {
            userProvider.saveUser(user)
        }
    }

    fun sauvegarder() {
        val user = userProvider.getUser(currentUserId)
        if (user != null) {
            userProvider.saveUser(user)
        }
    }
}