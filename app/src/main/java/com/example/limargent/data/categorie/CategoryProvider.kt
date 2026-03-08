package com.example.limargent.data.categorie

import com.example.limargent.domain.categorie.CategoryRepository
import com.example.limargent.domain.model.User
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
    private fun getUserId(): String {
        if (auth.currentUser != null) {
            return auth.currentUser!!.uid
        } else {
            return ""
        }
    }

    override suspend fun getCategories(): List<String> {
        val uid = getUserId()
        if (uid == "") {
            return ArrayList<String>()
        }

        try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists() == true) {
                val user = doc.toObject(User::class.java)
                if (user != null) {
                    return user.categories
                } else {
                    return ArrayList<String>()
                }
            } else {
                return ArrayList<String>()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList<String>()
        }
    }

    override suspend fun addCategorie(nom: String) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayUnion(nom))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun supprimer(nom: String) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayRemove(nom))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCategoriesFlow(): Flow<List<String>> {
        return callbackFlow {
            val uid = getUserId()

            if (uid == "") {
                trySend(ArrayList<String>())
                close()
                return@callbackFlow
            }
            val listener = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(ArrayList<String>())
                        close()
                    } else {
                        try {
                            // Au lieu de snapshot?.toObject(...)?.categories ?: emptyList()
                            if (snapshot != null) {
                                val user = snapshot.toObject(User::class.java)
                                if (user != null) {
                                    trySend(user.categories)
                                } else {
                                    trySend(ArrayList<String>())
                                }
                            } else {
                                trySend(ArrayList<String>())
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            trySend(ArrayList<String>())
                        }
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }
}