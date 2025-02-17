package edu.kitt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
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
            call.respond(listOf("Project 1", "Project 2", "Project 3"))
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

        }

        post {

        }

        get("/{id}") {

        }
    }
}
