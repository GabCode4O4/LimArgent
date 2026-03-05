package com.example.limargent.Data.Movements

import com.example.limargent.Domain.MoneyMovement
import com.example.limargent.Domain.MovType
import com.example.limargent.Domain.Mouvements.MovementsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovementsProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MovementsRepository {

    private val UserId: String?
        get() = auth.currentUser?.uid

    override suspend fun getMouvements(): List<MoneyMovement> {
        val uid = UserId ?: return emptyList()
        return try {
            val querySnapshot = firestore.collection("users").document(uid)
                .collection("mouvements").get().await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.toMoneyMovement()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun addMouvement(mouvement: MoneyMovement) {
        val uid = UserId ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").add(mouvement.toFirestoreMap())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteMouvement(mouvement: MoneyMovement) {
        val uid = UserId ?: return
        val mouvementId = mouvement.id ?: return
        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvementId).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getMouvementsFlow(): Flow<List<MoneyMovement>> = callbackFlow {
        val uid = UserId
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