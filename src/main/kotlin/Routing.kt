package edu.kitt

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.entries.CommentEntry
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// TODO: some routes are useless because the same info can be obtained in other ways
// (e.g. GET /issues/{id}/comments -> GET /issues/{id})

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
            val comment = call.receive<CommentEntry>()

            val issueID = call.parameters["id"]?.toIntOrNull()
            if (issueID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@post
            }

            if (commentRepository.createComment(comment) == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")
                return@post
            }

            call.respond(HttpStatusCode.Created, "Created")
        }

        put("/{issueID}/comments/{commentID}") {
            val comment = call.receive<CommentEntry>()

            val issueID = call.parameters["issueID"]?.toIntOrNull()
            if (issueID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@put
            }

            val commentID = call.parameters["commentID"]?.toIntOrNull()
            if (commentID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@put
            }

            if (comment.id != commentID || comment.issueID != issueID) {
                call.respond(HttpStatusCode.BadRequest, "Comment must be consistent with its URL")
                return@put
            }

            if (commentRepository.editComment(comment) == null) {
                call.respond(HttpStatusCode.NotFound, "Comment requested not found")
                return@put
            }

            call.respond(HttpStatusCode.Created, "Edited")
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
