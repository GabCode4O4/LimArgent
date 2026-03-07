package com.example.limargent.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(identifiant: String, motDePasse: String, confirmMotDePasse: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (identifiant.isBlank() || motDePasse.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez remplir tous les champs."))
        }
        if (motDePasse != confirmMotDePasse) {
            return@withContext Result.failure(IllegalArgumentException("Les mots de passe ne correspondent pas."))
        }
        if (motDePasse.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères."))
        }
        try {
            val email = "$identifiant@limargent.app"
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, motDePasse).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val defaultCategories = listOf(
                    "Alimentation", "Logement", "Transports", "Santé", "Loisirs", "Épargne", "Salaire", "Autre"
                )
                val userMap = hashMapOf(
                    "identifiant" to identifiant,
                    "categories" to defaultCategories,
                    "preference" to emptyMap<String, Any>() // Utiliser une map vide pour les préférences
                )

                firestore.collection("users").document(firebaseUser.uid).set(userMap).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Une erreur inconnue est survenue lors de la création de l'utilisateur."))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Cet identifiant est déjà utilisé."))
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Une erreur est survenue."))
        }
    }
}
