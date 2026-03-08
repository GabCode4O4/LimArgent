package com.example.limargent.data.mouvements

import com.example.limargent.domain.model.Frequence
import com.example.limargent.domain.model.MoneyMovement
import com.example.limargent.domain.model.MovType
import com.example.limargent.domain.mouvements.MouvementRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
class MouvementProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : MouvementRepository {

    private fun getUserId(): String {
        if (auth.currentUser != null) {
            return auth.currentUser!!.uid
        } else {
            return ""
        }
    }

    override suspend fun addMouvement(mouvement: MoneyMovement): Result<Unit> {
        val uid = getUserId()
        if (uid == "") {
            return Result.failure(Exception("Utilisateur non connecté."))
        }

        try {
            val map = convertToMap(mouvement)

            firestore.collection("users").document(uid)
                .collection("mouvements").add(map).await()

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun deleteMouvement(mouvement: MoneyMovement): Result<Unit> {
        val uid = getUserId()
        if (uid == "") {
            return Result.failure(Exception("Utilisateur non connecté."))
        }

        if (mouvement.id == null || mouvement.id == "") {
            return Result.failure(Exception("ID de mouvement manquant."))
        }

        try {
            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvement.id).delete().await()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updateMouvement(mouvement: MoneyMovement): Result<Unit> {
        val uid = getUserId()
        if (uid == "") {
            return Result.failure(Exception("Utilisateur non connecté."))
        }

        if (mouvement.id == null || mouvement.id == "") {
            return Result.failure(Exception("ID de mouvement manquant."))
        }

        try {
            val map = convertToMap(mouvement)

            firestore.collection("users").document(uid)
                .collection("mouvements").document(mouvement.id)
                .set(map).await()

            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getMouvementsFlow(): Flow<List<MoneyMovement>> {
        return callbackFlow {
            val uid = getUserId()

            if (uid == "") {
                trySend(ArrayList<MoneyMovement>())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection("users").document(uid)
                .collection("mouvements")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(ArrayList<MoneyMovement>())
                        close()
                    } else {
                        if (snapshot != null) {
                            // On crée la liste à la main au lieu d'utiliser mapNotNull
                            val mouvementsList = ArrayList<MoneyMovement>()

                            for (i in 0 until snapshot.documents.size) {
                                val doc = snapshot.documents[i]
                                val mouv = convertToMouvement(doc)
                                if (mouv != null) {
                                    mouvementsList.add(mouv)
                                }
                            }

                            trySend(mouvementsList)
                        } else {
                            trySend(ArrayList<MoneyMovement>())
                        }
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    private fun convertToMap(mouvement: MoneyMovement): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map.put("titre", mouvement.titre)
        map.put("montant", mouvement.montant.toString())
        map.put("categorie", mouvement.categorie)

        val dateConvertie = Date.from(mouvement.date.atStartOfDay(ZoneOffset.UTC).toInstant())
        map.put("date", dateConvertie)

        map.put("type", mouvement.type.name)
        map.put("isRecurrent", mouvement.isRecurrent)
        map.put("frequence", mouvement.frequence.name)
        if (mouvement.nextDate != null) {
            val nextDateConvertie = Date.from(mouvement.nextDate.atStartOfDay(ZoneOffset.UTC).toInstant())
            map.put("nextDate", nextDateConvertie)
        } else {
            map.put("nextDate", null)
        }

        return map
    }

    private fun convertToMouvement(doc: DocumentSnapshot): MoneyMovement? {
        try {
            val titre = doc.getString("titre")!!

            val montantStr = doc.getString("montant")!!
            val montant = BigDecimal(montantStr)

            val categorie = doc.getString("categorie")!!

            var date = LocalDate.now()
            if (doc.getTimestamp("date") != null) {
                val timestamp = doc.getTimestamp("date")!!
                date = timestamp.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
            }

            val typeStr = doc.getString("type")!!
            val type = MovType.valueOf(typeStr)

            var isRecurrent = false
            if (doc.getBoolean("isRecurrent") != null) {
                isRecurrent = doc.getBoolean("isRecurrent")!!
            }

            var frequence = Frequence.AUCUNE
            val frequenceStr = doc.getString("frequence")
            if (frequenceStr != null) {
                try {
                    frequence = Frequence.valueOf(frequenceStr)
                } catch (e: Exception) {
                    frequence = Frequence.AUCUNE
                }
            }

            var nextDate: LocalDate? = null
            if (doc.getTimestamp("nextDate") != null) {
                val timestampNext = doc.getTimestamp("nextDate")!!
                nextDate = timestampNext.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
            }

            return MoneyMovement(
                titre = titre,
                montant = montant,
                categorie = categorie,
                date = date,
                type = type,
                id = doc.id,
                isRecurrent = isRecurrent,
                frequence = frequence,
                nextDate = nextDate
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}