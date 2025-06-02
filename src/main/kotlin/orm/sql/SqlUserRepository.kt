package edu.kitt.orm.sql

import edu.kitt.domainmodel.User
import edu.kitt.orm.UserRepository
import java.sql.Connection
import java.sql.Statement

class SqlUserRepository(private val connection: Connection) : UserRepository {

    companion object {
        private const val SELECT_USER_BY_ID = "SELECT id, email_address, username FROM users WHERE id = ?"
        private const val SELECT_ALL_USERS = "SELECT id, email_address, username FROM users"
    }

    override suspend fun getUserByID(uid: Int): User? {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID, Statement.RETURN_GENERATED_KEYS)
        statement.setInt(1, uid)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            val userId = resultSet.getInt("id")
            val emailAddress = resultSet.getString("email_address")
            val username = resultSet.getString("username")
            return User(userId, emailAddress, username)
        } else {
            return null
        }
    }

    override suspend fun getAllUsers(): List<User> {
        val statement = connection.prepareStatement(SELECT_ALL_USERS, Statement.RETURN_GENERATED_KEYS)
        val resultSet = statement.executeQuery()
        val users = mutableListOf<User>()
        while (resultSet.next()) {
            val userId = resultSet.getInt("id")
            val emailAddress = resultSet.getString("email_address")
            val username = resultSet.getString("username")
            users.add(User(userId, emailAddress, username))
        }
        return users
    }

}