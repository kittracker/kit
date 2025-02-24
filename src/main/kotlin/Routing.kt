package edu.kitt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.sql.Connection
import java.sql.DriverManager
import kotlinx.serialization.Serializable

fun Route.projectRoutes() {
    route("/projects") {
        get {
            var jack = User(0, "Jack", "<EMAIL>")
            call.respond(
                listOf(
                    Project(
                        1,
                        "Test Project",
                        "ooga booga",
                        false,
                        jack,
                        mutableListOf<User>(),
                        mutableListOf<Issue>()
                    ),
                    Project(2, "Test Project", "ooga booga", false, jack, mutableListOf<User>(), mutableListOf<Issue>())
                )
            )
        }

        post {

        }

        get("/{id}") {

        }

        put("/{id}") {

        }

        delete("/{id}") {

        }

        post("/{id}/collaborators") {

        }

        put("/{id}/archive") {

        }
    }
}

fun Route.issueRoutes() {
    route("/issues") {
        get {

        }

        post {

        }

        get("/{id}") {

        }

        put("/{id}") {

        }

        delete("/{id}") {

        }

        post("/{id}/comments") {

        }

        put("/{id}/comments") {

        }

        post("/{id}/links") {

        }

        put("/{id}/links") {

        }
    }
}

fun Route.userRoutes() {
    route("/users") {
        get {
            call.respond(userRepository.getAllUsers())
        }

        post {

        }

        get("/{id}") {
            val uid = call.parameters["id"]?.toIntOrNull()
            if (uid == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be a number")
                return@get
            }

            val user = userRepository.getUserByID(uid)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }

            call.respond(user)
        }
    }
}
