package com.example.limargent.Data.User

import com.example.limargent.Data.Movements.MovementsProvider
import com.example.limargent.Domain.DataClasse.User
import com.example.limargent.Domain.Users.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val movementsProvider: MovementsProvider
) : UserRepository {

    private val currentUserUid: String?
        get() = auth.currentUser?.uid

    override suspend fun getCurrentUser(): User? {
        val uid = currentUserUid ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            user?.mouvements = movementsProvider.getMouvements().toMutableList()
            user
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