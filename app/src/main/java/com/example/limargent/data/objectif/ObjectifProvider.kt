package com.example.limargent.data.objectif

import com.example.limargent.domain.model.ObjectifEpargne
import com.example.limargent.domain.objectif.ObjectifRepository
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
class ObjectifProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ObjectifRepository {

    private fun getUserId(): String {
        if (auth.currentUser != null) {
            return auth.currentUser!!.uid
        } else {
            return ""
        }
    }

    override suspend fun addObjectif(objectif: ObjectifEpargne) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            val map = convertToMap(objectif)

            // on ajoute l'objectif aux données de l'utilisateur avec firestore
            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id)
                .set(map)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateObjectif(objectif: ObjectifEpargne) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            val map = convertToMap(objectif)

            firestore.collection("users").document(uid)
                .collection("objectifs").document(objectif.id)
                .set(map)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteObjectif(id: String) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid)
                .collection("objectifs").document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getObjectifsFlow(): Flow<List<ObjectifEpargne>> {
        // même fonctionnement que la fonction de flux dans le category provider

        return callbackFlow {
            val uid = getUserId()
            if (uid == "") {
                trySend(ArrayList<ObjectifEpargne>())
                close()
                return@callbackFlow
            }

            val listener = firestore.collection("users").document(uid)
                .collection("objectifs")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(ArrayList<ObjectifEpargne>())
                        close()
                    } else {
                        if (snapshot != null) {
                            val objectifsList = ArrayList<ObjectifEpargne>()

                            for (i in 0 until snapshot.documents.size) {
                                val doc = snapshot.documents[i]

                                try {
                                    var nom = ""
                                    if (doc.getString("nom") != null) {
                                        nom = doc.getString("nom")!!
                                    }

                                    var montantCibleStr = "0.0"
                                    if (doc.getString("montantCible") != null) {
                                        montantCibleStr = doc.getString("montantCible")!!
                                    }
                                    val montantCible = BigDecimal(montantCibleStr)

                                    var montantActuelStr = "0.0"
                                    if (doc.getString("montantActuel") != null) {
                                        montantActuelStr = doc.getString("montantActuel")!!
                                    }
                                    val montantActuel = BigDecimal(montantActuelStr)

                                    var tauxInteretStr = "0.0"
                                    if (doc.getString("tauxInteret") != null) {
                                        tauxInteretStr = doc.getString("tauxInteret")!!
                                    }
                                    val tauxInteret = BigDecimal(tauxInteretStr)

                                    var dateEcheance: LocalDate? = null
                                    if (doc.getTimestamp("dateEcheance") != null) {
                                        val timestamp = doc.getTimestamp("dateEcheance")!!
                                        dateEcheance = timestamp.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
                                    }
                                    val obj = ObjectifEpargne(
                                        id = doc.id,
                                        nom = nom,
                                        montantCible = montantCible,
                                        montantActuel = montantActuel,
                                        dateEcheance = dateEcheance,
                                        tauxInteret = tauxInteret
                                    )

                                    objectifsList.add(obj)

                                } catch (e: Exception) {
                                    // on ignore l'erreur pour ce document et on passe au suivant
                                }
                            }

                            trySend(objectifsList)

                        } else {
                            trySend(ArrayList<ObjectifEpargne>())
                        }
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    private fun convertToMap(objectif: ObjectifEpargne): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map.put("nom", objectif.nom)
        map.put("montantCible", objectif.montantCible.toString())
        map.put("montantActuel", objectif.montantActuel.toString())
        map.put("tauxInteret", objectif.tauxInteret.toString())
        if (objectif.dateEcheance != null) {
            val dateConvertie = Date.from(objectif.dateEcheance.atStartOfDay(ZoneOffset.UTC).toInstant())
            map.put("dateEcheance", dateConvertie)
        } else {
            map.put("dateEcheance", null)
        }

        return map
    }
}