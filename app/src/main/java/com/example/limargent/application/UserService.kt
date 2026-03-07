package com.example.limargent.application

import com.example.limargent.domain.dataClasse.User
import com.example.limargent.domain.utilisateur.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserService @Inject constructor(
    private val userRepository: UserRepository
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            userRepository.getCurrentUser()
        }
    }

    override suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userRepository.updateUser(user)
        }
    }
}