package edu.kitt.orm

import edu.kitt.domainmodel.User

// Just to test some stuff
class InMemoryUserRepository : UserRepository {
    // No optimization needed
    private val users = mutableListOf<User>(
        User(1, "matteo@gmail.com", "cardisk"),
        User(2, "leonardo@gmail.com", "spectrev333"),
        User(3, "mirco@gmail.com", "mircocaneschi"),
    )

    override fun getUserByID(uid: Int): User? {
        return users.firstOrNull { it.id == uid }
    }

    override fun getAllUsers(): List<User> {
        return users
    }
}