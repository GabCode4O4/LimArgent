package com.example.limargent.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun authentification(identifiant: String, motDePasse: String): Result<Unit> {
        if (identifiant == "" || identifiant.trim() == "" || motDePasse == "" || motDePasse.trim() == "") {
            return Result.failure(IllegalArgumentException("L'identifiant et le mot de passe ne peuvent pas être vides."))
        }

        try {
            // on essaie la connexion avec une méthode firebase, si l'authentification échoue, on lance des exceptions
            val email = identifiant + "@limargent.app"
            firebaseAuth.signInWithEmailAndPassword(email, motDePasse).await()

            return Result.success(Unit)

        } catch (e: FirebaseAuthInvalidUserException) {
            return Result.failure(Exception("Aucun compte trouvé pour cet identifiant."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return Result.failure(Exception("Mot de passe incorrect."))
        } catch (e: Exception) {
            return Result.failure(Exception("Une erreur de connexion est survenue."))
        }
    }
}