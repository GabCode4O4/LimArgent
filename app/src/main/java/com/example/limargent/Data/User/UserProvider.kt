package com.example.limargent.Data.User

import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import com.example.limargent.Domain.Objectif.ObjectifRepository
import com.example.limargent.Domain.ObjectifEpargne
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class UserProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRepository, ObjectifRepository {

    private val currentUserUid: String?
        get() = auth.currentUser?.uid

    override suspend fun getCurrentUser(): User? {
        val uid = currentUserUid ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
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
            firestore.collection("users").document(uid).set(userMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addCategoryToCurrentUser(categorie: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayUnion(categorie))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeCategoryFromCurrentUser(categorie: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid).update("categories", FieldValue.arrayRemove(categorie))
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

    fun addMovement(mouvement: MoneyMovement) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").add(mouvement.toFirestoreMap())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateMovement(mouvement: MoneyMovement) {
        val uid = currentUserUid ?: return
        val mouvementId = mouvement.id ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvementId).set(mouvement.toFirestoreMap())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteMovement(mouvementId: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvementId).delete()
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

    fun getMouvementsFlow(): Flow<List<MoneyMovement>> = callbackFlow {
        val uid = currentUserUid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(uid)
            .collection("mouvements")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    close()
                    return@addSnapshotListener
                }
                val mouvementsList = snapshot?.documents?.mapNotNull { it.toMoneyMovement() } ?: emptyList()
                trySend(mouvementsList)
            }
        awaitClose { listener.remove() }
    }

    fun getCategoriesFlow(): Flow<List<String>> = callbackFlow {
        val uid = currentUserUid
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
                val user = snapshot?.toObject(User::class.java)
                trySend(user?.categories ?: emptyList())
            }

        awaitClose { listener.remove() }
    }
    override suspend fun addObjectif(objectif: ObjectifEpargne) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id).set(objectif.toMap())
        } catch (e: Exception) { e.printStackTrace() }
    }

    override suspend fun updateObjectif(objectif: ObjectifEpargne) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id).set(objectif.toMap())
        } catch (e: Exception) { e.printStackTrace() }
    }

    override suspend fun deleteObjectif(id: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(id).delete()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> = callbackFlow {
        val uid = currentUserUid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(uid)
            .collection("objectifs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    close()
                    return@addSnapshotListener
                }
                val objectifs = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ObjectifEpargne(
                            id = doc.id,
                            nom = doc.getString("nom") ?: "",
                            montantCible = doc.getDouble("montantCible") ?: 0.0,
                            montantActuel = doc.getDouble("montantActuel") ?: 0.0,
                            dateEcheance = doc.getTimestamp("dateEcheance")?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(objectifs)
            }
        awaitClose { listener.remove() }
    }

    private fun ObjectifEpargne.toMap(): Map<String, Any?> {
        return mapOf(
            "nom" to nom,
            "montantCible" to montantCible,
            "montantActuel" to montantActuel,
            "dateEcheance" to dateEcheance?.let { Date.from(it.atStartOfDay(ZoneOffset.UTC).toInstant()) }
        )
    }
}
