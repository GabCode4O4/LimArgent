package com.example.limargent.application.classe

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val UserAct = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = UserAct
    init {
        firebaseAuth.addAuthStateListener { auth ->
            UserAct.value = auth.currentUser
        }
    }
    fun logout() {
        firebaseAuth.signOut()
    }
}