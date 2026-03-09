package com.example.limargent.data.preference

import com.example.limargent.domain.preferences.PreferenceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceProvider @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PreferenceRepository {
    private fun getUserId(): String {
        if (auth.currentUser != null) {
            return auth.currentUser!!.uid
        } else {
            return ""
        }
    }

    override suspend fun getTheme(): String {
        val uid = getUserId()
        if (uid == "") {
            return "system"
        }

        // on cherche dans la collection de l'utilisateur son thème dans ses préférences, si y'en a pas, on prend celui par défaut du système
        try {
            val doc = firestore.collection("users").document(uid).get().await()
            val themePref = doc.getString("preference.theme")
            if (themePref != null) {
                return themePref
            } else {
                return "system"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "system"
        }
    }

    override suspend fun setTheme(theme: String) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid).update("preference.theme", theme).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getSeuilBudget(): Double {
        val uid = getUserId()

        // on met un seuil à 100 euros par défaut
        if (uid == "") {
            return 100.0
        }

        try {
            val doc = firestore.collection("users").document(uid).get().await()

            val seuil = doc.getDouble("preference.seuilBudget")
            if (seuil != null) {
                return seuil
            } else {
                return 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 100.0
        }
    }

    override suspend fun setSeuilBudget(seuil: Double) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid).update("preference.seuilBudget", seuil).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun isNotificationActive(): Boolean {
        val uid = getUserId()
        if (uid == "") {
            return false
        }

        // simple vérif des notifs

        try {
            val doc = firestore.collection("users").document(uid).get().await()

            val isActive = doc.getBoolean("preference.notificationsActive")

            if (isActive != null) {
                if (isActive == true) {
                    return true
                } else {
                    return false
                }
            } else {
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun setNotificationActive(active: Boolean) {
        val uid = getUserId()
        if (uid == "") {
            return
        }

        try {
            firestore.collection("users").document(uid).update("preference.notificationsActive", active).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}