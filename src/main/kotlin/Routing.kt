package edu.kitt

import edu.kitt.orm.entries.CommentEntry
import edu.kitt.orm.entries.IssueEntry
import edu.kitt.orm.requests.CommentEntryRequest
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
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
            val issue = call.receive<IssueEntryRequest>()
            val created = issueRepository.createIssue(issue)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create issue")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
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

        put {
            val entry = call.receive<IssueEntryRequest>()

            val edited = issueRepository.editIssue(entry)
            if (edited == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@put
            }

            call.respond(edited)
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@delete
            }

            if (!issueRepository.deleteIssue(id)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove comment")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.linkRoutes() {
    route("/links") {
        post {

        }

        delete {

        }
    }
}

fun Route.commentRoutes() {
    route("/comments") {
        post {
            val comment = call.receive<CommentEntryRequest>()

            val created = commentRepository.createComment(comment)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        put {
            val comment = call.receive<CommentEntryRequest>()

            val edited = commentRepository.editComment(comment)
            if (edited == null) {
                call.respond(HttpStatusCode.NotFound, "Comment requested not found")
                return@put
            }

            call.respond(edited)
        }

        delete("/{id}") {
            val commentID = call.parameters["id"]?.toIntOrNull()
            if (commentID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@delete
            }

            if (!commentRepository.removeCommentByID(commentID)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove comment")
                return@delete
            }

            call.respond("Removed")
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
