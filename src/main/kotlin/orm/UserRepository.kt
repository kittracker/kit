package edu.kitt.orm

import edu.kitt.domainmodel.User

interface UserRepository {
    fun getUserByID(uid: Int): User?
    fun getAllUsers(): List<User>
    fun getUser(username: String, password: String): User?
}