package com.example.limargent.Application

import com.example.limargent.Data.User.UserProvider
import com.example.limargent.Domain.User
import com.example.limargent.Domain.Users.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserService @Inject constructor(
    private val userProvider: UserProvider
) : UserRepository {

    override suspend fun getUser(identifiant: String): User? {
        return withContext(Dispatchers.IO) {
            userProvider.getUser(identifiant)
        }
    }

    override suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            userProvider.saveUser(user)
        }
    }

    override suspend fun deleteUser(identifiant: String) {
        withContext(Dispatchers.IO) {
            userProvider.deleteUser(identifiant)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return withContext(Dispatchers.IO) {
            userProvider.getUsers()
        }
    }
}