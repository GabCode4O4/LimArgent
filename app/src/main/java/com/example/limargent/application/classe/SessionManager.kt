package com.example.limargent.application.classe

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
//Classe permettant de gérer la session de l'utilisateur
class SessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val UserAct = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser) //récupère l'utilisateur actuellement connecté
    val currentUser: StateFlow<FirebaseUser?> = UserAct
    init { //Listener qui met à jour l'utilisateur
        firebaseAuth.addAuthStateListener { auth ->
            UserAct.value = auth.currentUser
        }
    }
    fun logout() {
        firebaseAuth.signOut()
    }
}