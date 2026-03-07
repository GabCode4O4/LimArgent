package com.example.limargent.data.user

import com.example.limargent.domain.dataClasse.User
import com.example.limargent.domain.utilisateur.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository {

    private val currentUserUid: String?
        get() = auth.currentUser?.uid

    override suspend fun getCurrentUser(): User? {
        val uid = currentUserUid ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateUser(user: User) {
        val uid = currentUserUid ?: return
        try {
            val userMap = user.toFirestoreMap()
            firestore.collection("users").document(uid).set(userMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun User.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "identifiant" to identifiant,
            "categories" to categories,
            "preference" to preference
        )
    }
}