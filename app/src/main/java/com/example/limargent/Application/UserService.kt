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

    override suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            userProvider.getCurrentUser()
        }
    }

    override suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userProvider.updateUser(user)
        }
    }
}