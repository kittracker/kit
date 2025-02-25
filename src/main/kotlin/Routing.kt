package edu.kitt

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.projectRoutes() {
    route("/projects") {
        get {
            call.respond(projectRepository.getAllProjects())
        }

        post {

        }

        get("/{id}") {
            val projectID = call.parameters["id"]?.toIntOrNull()
            if (projectID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@get
            }

            val project = projectRepository.getProjectByID(projectID)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@get
            }

            call.respond(project)
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
            call.respond(issueRepository.getAllIssues())
        }

        post {

        }

        get("/{id}") {
            val issueID = call.parameters["id"]?.toIntOrNull()
            if (issueID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@get
            }

            val issue = issueRepository.getIssueByID(issueID)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@get
            }

            call.respond(issue)
        }

        put("/{id}") {

        }

        delete("/{id}") {

        }

        get("/{id}/comments") {
            val issueID = call.parameters["id"]?.toIntOrNull()
            if (issueID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@get
            }

            val comments = commentRepository.getCommentsByIssueID(issueID)
            call.respond(comments)
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
