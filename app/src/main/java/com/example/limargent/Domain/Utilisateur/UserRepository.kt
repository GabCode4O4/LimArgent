package com.example.limargent.Domain.Users

import com.example.limargent.Domain.User

interface UserRepository {
    suspend fun getUser(identifiant: String): User?
    suspend fun saveUser(user: User)
    suspend fun deleteUser(identifiant: String)
    suspend fun getAllUsers(): List<User>
}