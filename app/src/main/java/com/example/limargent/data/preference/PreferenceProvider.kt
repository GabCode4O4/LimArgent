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

    // Raccourci pour récupérer l'ID de l'utilisateur connecté
    private val userId: String? get() = auth.currentUser?.uid

    override suspend fun getTheme(): String {
        val uid = userId ?: return "system"
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("preference.theme") ?: "system"
        } catch (e: Exception) { "system" }
    }

    override suspend fun setTheme(theme: String) {
        val uid = userId ?: return
        try {
            // Met à jour uniquement le champ theme dans l'objet preference
            firestore.collection("users").document(uid).update("preference.theme", theme).await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override suspend fun getSeuilBudget(): Double {
        val uid = userId ?: return 100.0
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getDouble("preference.seuilBudget") ?: 100.0
        } catch (e: Exception) { 100.0 }
    }

    override suspend fun setSeuilBudget(seuil: Double) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).update("preference.seuilBudget", seuil).await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    override suspend fun isNotificationActive(): Boolean {
        val uid = userId ?: return true
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getBoolean("preference.notificationsActive") ?: true
        } catch (e: Exception) { true }
    }

    override suspend fun setNotificationActive(active: Boolean) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).update("preference.notificationsActive", active).await()
        } catch (e: Exception) { e.printStackTrace() }
    }
}