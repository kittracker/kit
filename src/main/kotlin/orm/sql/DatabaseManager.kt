package edu.kitt.orm.sql

import java.sql.Connection

class DatabaseManager (connection: Connection){
    companion object {
        private const val DATABASE_INIT = "CREATE TABLE issues IF NOT EXISTS;"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(DATABASE_INIT)
    }

}