package com.example.limargent.Application

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun authentification(identifiant: String, motDePasse: String): Boolean {
        return try {
            val email = "$identifiant@limargent.app"
            firebaseAuth.signInWithEmailAndPassword(email, motDePasse).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}