package edu.kitt.orm

import edu.kitt.checkPassword
import edu.kitt.domainmodel.User
import edu.kitt.hashPassword
import edu.kitt.orm.requests.SignupRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedUserRepository : UserRepository {
    override suspend fun getUserByID(uid: Int): User? = withContext(Dispatchers.IO) {
        transaction {
            UserDAO.findById(uid)?.let(::mapUserDAOtoUser)
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val userDAO = UserDAO.find { Users.userName eq username }.firstOrNull()
            if (userDAO == null) return@newSuspendedTransaction null
            mapUserDAOtoUser(userDAO)
        }
    }

    override suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        transaction {
            UserDAO.all().map(::mapUserDAOtoUser)
        }
    }

    override suspend fun getUser(username: String, password: String): User? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val userDAO = UserDAO.find {
                Users.userName eq username
            }.firstOrNull()
            if (userDAO == null) return@newSuspendedTransaction null
            if (checkPassword(password, userDAO.passwordHash)) {
                return@newSuspendedTransaction mapUserDAOtoUser(userDAO)
            } else {
                return@newSuspendedTransaction null
            }
        }
    }

    override suspend fun createUser(request: SignupRequest): User? = newSuspendedTransaction(Dispatchers.IO) {
        val userDAO = UserDAO.new {
            emailAddress = request.email
            userName = request.username
            passwordHash = hashPassword(request.password)
        }
        mapUserDAOtoUser(userDAO)
    }

}