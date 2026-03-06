package com.example.limargent.Application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun authentification(identifiant: String, motDePasse: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (identifiant.isBlank() || motDePasse.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("L'identifiant et le mot de passe ne peuvent pas être vides."))
        }

        try {
            val email = "$identifiant@limargent.app"
            firebaseAuth.signInWithEmailAndPassword(email, motDePasse).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("Aucun compte trouvé pour cet identifiant."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Mot de passe incorrect."))
        } catch (e: Exception) {
            Result.failure(Exception("Une erreur de connexion est survenue."))
        }
    }
}
