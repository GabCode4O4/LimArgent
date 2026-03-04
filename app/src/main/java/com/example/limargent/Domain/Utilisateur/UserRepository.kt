package com.example.limargent.Domain.Users

import com.example.limargent.Domain.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun updateUser(user: User)
}