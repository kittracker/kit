package edu.kitt.orm

import edu.kitt.domainmodel.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedUserRepository : UserRepository {
    override suspend fun getUserByID(uid: Int): User? = withContext(Dispatchers.IO) {
        transaction {
            UserDAO.findById(uid.toUInt())?.let {
                User(
                    id = it.id.value.toInt(),
                    emailAddress = it.emailAddress,
                    username = it.userName
                )
            }
        }
    }

    override suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        transaction {
            UserDAO.all().map {
                User(
                    id = it.id.value.toInt(),
                    emailAddress = it.emailAddress,
                    username = it.userName
                )
            }
        }
    }
}