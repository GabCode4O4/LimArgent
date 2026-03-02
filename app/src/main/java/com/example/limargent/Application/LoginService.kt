package com.example.limargent.Application

import com.example.limargent.Data.SessionManager
import com.example.limargent.Domain.Users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginService @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    suspend fun authentification(identifiant: String, motDePasse: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userRepository.getUser(identifiant)
            if (user != null && user.motDePasse == motDePasse) {
                sessionManager.login(identifiant)
                true
            } else {
                false
            }
        }
    }
}