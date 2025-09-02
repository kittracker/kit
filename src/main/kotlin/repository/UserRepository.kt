package edu.kitt.repository

import edu.kitt.domainmodel.User
import edu.kitt.repository.requests.SignupRequest

interface UserRepository {
    suspend fun getUserByID(uid: Int): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(username: String, password: String): User?
    suspend fun createUser(request: SignupRequest): User?
}