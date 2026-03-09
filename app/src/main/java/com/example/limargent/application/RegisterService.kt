package com.example.limargent.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(identifiant: String, motDePasse: String, confirmMotDePasse: String): Result<Unit> {
        if (identifiant == "" || identifiant.trim() == "" || motDePasse == "" || motDePasse.trim() == "") {
            return Result.failure(IllegalArgumentException("Veuillez remplir tous les champs."))
        }
        if (motDePasse.equals(confirmMotDePasse) == false) {
            return Result.failure(IllegalArgumentException("Les mots de passe ne correspondent pas."))
        }
        if (motDePasse.length < 6) {
            return Result.failure(IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères."))
        }

        // On essaie de gérer tous les cas d'erreurs de typo et ensuite on essaie l'inscription avec une méthode firebase

        try {
            val email = identifiant + "@limargent.app"
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, motDePasse).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val defaultCategories = ArrayList<String>()
                defaultCategories.add("Alimentation")
                defaultCategories.add("Logement")
                defaultCategories.add("Transports")
                defaultCategories.add("Santé")
                defaultCategories.add("Loisirs")
                defaultCategories.add("Épargne")
                defaultCategories.add("Salaire")
                defaultCategories.add("Autre")

                val userMap = HashMap<String, Any>()
                userMap.put("identifiant", identifiant)
                userMap.put("categories", defaultCategories)

                val preferenceMap = HashMap<String, Any>()
                userMap.put("preference", preferenceMap)
                firestore.collection("users").document(firebaseUser.uid).set(userMap).await()

                return Result.success(Unit)
            } else {
                return Result.failure(Exception("Une erreur inconnue est survenue lors de la création de l'utilisateur."))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            return Result.failure(Exception("Cet identifiant est déjà utilisé."))
        } catch (e: Exception) {
            var message = e.localizedMessage
            if (message == null) {
                message = "Une erreur est survenue."
            }
            return Result.failure(Exception(message))
        }
    }
}