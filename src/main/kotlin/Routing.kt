package edu.kitt

import CollaboratorEntryRequest
import edu.kitt.orm.requests.CommentEntryRequest
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import edu.kitt.orm.requests.ProjectEntryRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.projectRoutes() {
    route("/projects") {
        get {
            call.respond(projectRepository.getAllProjects())
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

        post {
            val project = call.receive<ProjectEntryRequest>()
            val created = projectRepository.createProject(project)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create project")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        put {
            val entry = call.receive<ProjectEntryRequest>()

            val edited = projectRepository.editProject(entry)
            if (edited == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
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

            if (!projectRepository.deleteProject(id)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove project")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.collaboratorRoutes() {
    route("/collaborators") {
        post {
            val collaborator = call.receive<CollaboratorEntryRequest>()
            val created = projectRepository.addCollaboratorToProject(collaborator)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add collaborator to project")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val collaborator = call.receive<CollaboratorEntryRequest>()
            if (!projectRepository.removeCollaboratorToProject(collaborator)) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@delete
            }

            call.respond("Removed")
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
            val link = call.receive<IssueLinkEntryRequest>()

            val created = issueRepository.linkIssues(link)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create link")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val link = call.receive<IssueLinkEntryRequest>()
            if (!issueRepository.deleteLink(link)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove link")
                return@delete
            }

            call.respond("Removed")
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
