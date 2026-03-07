package com.example.limargent.domain.utilisateur

import com.example.limargent.domain.dataClasse.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun updateUser(user: User)
}