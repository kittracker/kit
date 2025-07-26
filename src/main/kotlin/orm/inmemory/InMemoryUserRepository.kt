package edu.kitt.orm.inmemory

import edu.kitt.domainmodel.User
import edu.kitt.orm.UserRepository
import edu.kitt.orm.entries.UserEntry
import edu.kitt.orm.requests.SignupRequest

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf<UserEntry>(
        UserEntry(1, "matteo@gmail.com", "cardisk", "", "", true),
        UserEntry(2, "leonardo@gmail.com", "spectrev333", "", "", true),
        UserEntry(3, "mirco@gmail.com", "mircocaneschi", "", "", true),
    )

    override suspend fun getUserByID(uid: Int): User? {
        val userEntry = users.firstOrNull { it.id == uid }
        if (userEntry == null) return null
        return User(
            userEntry.id!!,
            userEntry.emailAddress,
            userEntry.username,
            userEntry.firstName,
            userEntry.lastName,
            userEntry.notificationsActive,
        )
    }

    override suspend fun getUserByUsername(username: String): User? {
        val userEntry = users.firstOrNull { it.username == username }
        if (userEntry == null) return null
        return User(
            userEntry.id!!,
            userEntry.emailAddress,
            userEntry.username,
            userEntry.firstName,
            userEntry.lastName,
            userEntry.notificationsActive,
        )
    }

    override suspend fun getAllUsers(): List<User> {
        return users.map { User(it.id!!, it.emailAddress, it.username, it.firstName, it.lastName, it.notificationsActive) }
    }

    override suspend fun getUser(username: String, password: String): User? {
        val user = users.firstOrNull { it.username == username && it.username == password }
        if (user == null) return null
        return User(user.id!!, user.emailAddress, user.username, user.firstName, user.lastName, user.notificationsActive)
    }

    override suspend fun createUser(request: SignupRequest): User? {
        val newId = (users.maxOfOrNull { it.id } ?: 0) + 1
        val newUser = UserEntry(
            id = newId,
            emailAddress = request.email,
            username = request.username,
            firstName = request.firstName,
            lastName = request.lastName,
            notificationsActive = request.notificationsActive,
        )
        users.add(newUser)
        return User(newUser.id!!, newUser.emailAddress, newUser.username, newUser.firstName, newUser.lastName, newUser.notificationsActive)
    }
}