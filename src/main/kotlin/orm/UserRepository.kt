package edu.kitt.orm

import edu.kitt.domainmodel.User

interface UserRepository {
    suspend fun getUserByID(uid: Int): User?
    suspend fun getAllUsers(): List<User>
    suspend fun getUser(username: String, password: String): User?
}