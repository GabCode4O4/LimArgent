package com.example.limargent.data.categorie

import com.example.limargent.domain.categorie.CategoryRepository
import com.example.limargent.domain.dataClasse.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CategoryRepository {

    private val userId: String?
        get() = auth.currentUser?.uid

    override suspend fun getCategories(): List<String> {
        val uid = userId ?: return emptyList()
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            user?.categories ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun addCategorie(nom: String) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayUnion(nom))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun supprimer(nom: String) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayRemove(nom))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCategoriesFlow(): Flow<List<String>> = callbackFlow {
        val uid = userId
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    close()
                    return@addSnapshotListener
                }
                try {
                    val user = snapshot?.toObject(User::class.java)
                    trySend(user?.categories ?: emptyList())
                } catch (e: Exception) {
                    e.printStackTrace()
                    trySend(emptyList())
                }
            }
        awaitClose { listener.remove() }
    }
}