package com.example.limargent.Application

import com.example.limargent.Data.SessionManager
import com.example.limargent.Domain.User
import com.example.limargent.Domain.Users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterService @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    suspend fun register(identifiant: String, motDePasse: String): String? {
        return withContext(Dispatchers.IO) {
            val existingUser = userRepository.getUser(identifiant)
            if (existingUser != null) {
                return@withContext "Cet identifiant est déjà utilisé."
            }
            val defaultCategories = mutableListOf(
                "Alimentation", "Logement", "Transports", "Santé", "Loisirs", "Épargne", "Salaire", "Autre"
            )
            val newUser = User(
                identifiant = identifiant,
                motDePasse = motDePasse,
                mouvements = mutableListOf(),
                categories = defaultCategories
            )

            userRepository.saveUser(newUser)
            sessionManager.login(identifiant)
            null
        }
    }
}