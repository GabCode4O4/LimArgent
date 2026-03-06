package com.example.limargent.Application

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.DataClasse.User
import com.example.limargent.Domain.Users.UserRepository
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