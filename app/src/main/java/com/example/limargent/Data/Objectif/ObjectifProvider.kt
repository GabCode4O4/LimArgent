package com.example.limargent.Data.Objectifs

import com.example.limargent.Domain.DataClasse.ObjectifEpargne
import com.example.limargent.Domain.Objectif.ObjectifRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectifProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ObjectifRepository {

    private val currentUserUid: String?
        get() = auth.currentUser?.uid

    override suspend fun addObjectif(objectif: ObjectifEpargne) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id)
                .set(objectif.toMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateObjectif(objectif: ObjectifEpargne) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id)
                .set(objectif.toMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteObjectif(id: String) {
        val uid = currentUserUid ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                            dateEcheance = doc.getTimestamp("dateEcheance")?.toDate()?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate(),
                            tauxInteret = doc.getDouble("tauxInteret") ?: 0.0
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
            "dateEcheance" to dateEcheance?.let { Date.from(it.atStartOfDay(ZoneOffset.UTC).toInstant()) },
            "tauxInteret" to tauxInteret
        )
    }
}