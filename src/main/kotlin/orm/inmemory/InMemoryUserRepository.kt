package edu.kitt.orm

import edu.kitt.domainmodel.User
import edu.kitt.orm.entries.UserEntry

class InMemoryUserRepository : UserRepository {
    private val users = listOf(
        UserEntry(1, "matteo@gmail.com", "cardisk"),
        UserEntry(2, "leonardo@gmail.com", "spectrev333"),
        UserEntry(3, "mirco@gmail.com", "mircocaneschi"),
    )

    override suspend fun getUserByID(uid: Int): User? {
        val userEntry = users.firstOrNull { it.id == uid }
        if (userEntry == null) return null
        return User(
            userEntry.id!!,
            userEntry.emailAddress,
            userEntry.username,
        )
    }

    override suspend fun getAllUsers(): List<User> {
        return users.map { User(it.id!!, it.emailAddress, it.username) }
    }

    override suspend fun getUser(username: String, password: String): User? {
        val user = users.firstOrNull { it.username == username && it.username == password }
        if (user == null) return null
        return User(user.id!!, user.emailAddress, user.username)
    }
}