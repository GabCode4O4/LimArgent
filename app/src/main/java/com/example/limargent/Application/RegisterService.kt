package com.example.limargent.Application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun register(identifiant: String, motDePasse: String): String? {
        return try {
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
                    "preference" to emptyList<String>()
                )

                firestore.collection("users").document(firebaseUser.uid).set(userMap).await()
                null // Succès
            } else {
                "Une erreur inconnue est survenue lors de la création de l'utilisateur."
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            "Cet identifiant est déjà utilisé."
        } catch (e: Exception) {
            e.localizedMessage ?: "Une erreur est survenue."
        }
    }
}