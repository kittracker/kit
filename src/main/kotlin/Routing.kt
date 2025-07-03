package edu.kitt

import CollaboratorEntryRequest
import edu.kitt.orm.requests.CommentEntryRequest
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import edu.kitt.orm.requests.ProjectEntryRequest
import io.ktor.http.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.projectRoutes(repos: Repositories) {
    route("/projects") {
        get {
            call.respond(repos.projectRepository.getAllProjects())
        }

        get("/{id}") {
            val projectID = call.parameters["id"]?.toIntOrNull()
            if (projectID == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be an integer")
                return@get
            }

            val project = repos.projectRepository.getProjectByID(projectID)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@get
            }

            call.respond(project)
        }

        post {
            val project = call.receive<ProjectEntryRequest>()
            val created = repos.projectRepository.createProject(project)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create project")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        put {
            val entry = call.receive<ProjectEntryRequest>()

            val edited = repos.projectRepository.editProject(entry)
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

            if (!repos.projectRepository.deleteProject(id)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove project")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.collaboratorRoutes(repos: Repositories) {
    route("/collaborators") {
        post {
            val collaborator = call.receive<CollaboratorEntryRequest>()
            val created = repos.projectRepository.addCollaboratorToProject(collaborator)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add collaborator to project")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val collaborator = call.receive<CollaboratorEntryRequest>()
            if (!repos.projectRepository.removeCollaboratorToProject(collaborator)) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.issueRoutes(repos: Repositories) {
    route("/issues") {
        get {
            call.respond(repos.issueRepository.getAllIssues())
        }

        post {
            val issue = call.receive<IssueEntryRequest>()
            val created = repos.issueRepository.createIssue(issue)
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

            val issue = repos.issueRepository.getIssueByID(issueID)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@get
            }

            call.respond(issue)
        }

        put {
            val entry = call.receive<IssueEntryRequest>()

            val edited = repos.issueRepository.editIssue(entry)
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

            if (!repos.issueRepository.deleteIssue(id)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove comment")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.linkRoutes(repos: Repositories) {
    route("/links") {
        post {
            val link = call.receive<IssueLinkEntryRequest>()

            val created = repos.issueRepository.linkIssues(link)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create link")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val link = call.receive<IssueLinkEntryRequest>()
            if (!repos.issueRepository.deleteLink(link)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove link")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.commentRoutes(repos: Repositories) {
    route("/comments") {
        post {
            val comment = call.receive<CommentEntryRequest>()

            val created = repos.commentRepository.createComment(comment)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        put {
            val comment = call.receive<CommentEntryRequest>()

            val edited = repos.commentRepository.editComment(comment)
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

            if (!repos.commentRepository.removeCommentByID(commentID)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove comment")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.userRoutes(repos: Repositories) {
    route("/users") {
        get {
            call.respond(repos.userRepository.getAllUsers())
        }

        post {

        }

        get("/{id}") {
            val uid = call.parameters["id"]?.toIntOrNull()
            if (uid == null) {
                call.respond(HttpStatusCode.BadRequest, "ID must be a number")
                return@get
            }

            val user = repos.userRepository.getUserByID(uid)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }

            call.respond(user)
        }

        get("/username/{username}") {
            val username = call.parameters["username"]
            if (username == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid username")
                return@get
            }

            val user = repos.userRepository.getUserByUsername(username)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }

            call.respond(user)
        }
    }
}
