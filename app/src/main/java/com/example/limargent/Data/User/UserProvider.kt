package com.example.limargent.Data.User

import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import com.example.limargent.Domain.User
import com.example.limargent.Domain.Users.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
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
            val user = doc.toObject<User>()
            user?.mouvements = getMouvements()
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
            firestore.collection("users").document(uid).set(userMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addCategoryToCurrentUser(categorie: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayUnion(categorie)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun removeCategoryFromCurrentUser(categorie: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayRemove(categorie)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getMouvements(): MutableList<MoneyMovement> {
        val uid = currentUserUid ?: return mutableListOf()
        return try {
            val querySnapshot = firestore.collection("users").document(uid)
                .collection("mouvements").get().await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.toMoneyMovement()
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    suspend fun addMovement(mouvement: MoneyMovement) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").add(mouvement.toFirestoreMap()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateMovement(mouvement: MoneyMovement) {
        val uid = currentUserUid ?: return
        val mouvementId = mouvement.id ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvementId).set(mouvement.toFirestoreMap()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteMovement(mouvementId: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvementId).delete().await()
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

    private fun MoneyMovement.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "titre" to titre,
            "montant" to montant.toString(),
            "categorie" to categorie,
            "date" to Date.from(date.atStartOfDay(ZoneOffset.UTC).toInstant()),
            "type" to type.name
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toMoneyMovement(): MoneyMovement? {
        return try {
            val titre = getString("titre")!!
            val montant = BigDecimal(getString("montant")!!)
            val categorie = getString("categorie")!!
            val date = (getTimestamp("date")?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()) ?: LocalDate.now()
            val type = MovType.valueOf(getString("type")!!)
            MoneyMovement(titre, montant, categorie, date, type, id = this.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// You might need to add an 'id' field to your MoneyMovement data class
// data class MoneyMovement(..., val id: String? = null)
// And adjust the User data class to not have a password field
// data class User(..., val motDePasse: String = "", ...) -> remove motDePasse
