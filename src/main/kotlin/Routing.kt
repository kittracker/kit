package edu.kitt

import CollaboratorEntryRequest
import edu.kitt.orm.requests.CommentEntryRequest
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import edu.kitt.orm.requests.ProjectEntryRequest
import io.ktor.client.call.body
import io.ktor.http.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.projectRoutes(repos: Repositories, mailer: Mailer) {
    route("/projects") {
        get {
            val principal = call.principal<JWTPrincipal>()
            val id = principal!!.payload.getClaim("userId").asInt()
            call.respond(repos.projectRepository.getProjectsByUserID(id))
        }

        get("/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

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

            if (project.owner.id != userId &&
                project.collaborators.find { collaborator -> collaborator.id == userId } == null) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
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
            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val entry = call.receive<ProjectEntryRequest>()

            val project = repos.projectRepository.getProjectByID(entry.id ?: 0)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@put
            }

            if (project.owner.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
                return@put
            }

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

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val project = repos.projectRepository.getProjectByID(id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@delete
            }

            if (project.owner.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
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

fun Route.collaboratorRoutes(repos: Repositories, mailer: Mailer) {
    route("/collaborators") {
        post {
            val collaborator = call.receive<CollaboratorEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val project = repos.projectRepository.getProjectByID(collaborator.projectID)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@post
            }

            if (project.owner.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
                return@post
            }

            val created = repos.projectRepository.addCollaboratorToProject(collaborator)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to add collaborator to project")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val collaborator = call.receive<CollaboratorEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val project = repos.projectRepository.getProjectByID(collaborator.projectID)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@delete
            }

            if (project.owner.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
                return@delete
            }

            if (!repos.projectRepository.removeCollaboratorToProject(collaborator)) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.issueRoutes(repos: Repositories, mailer: Mailer) {
    route("/issues") {
        // get {
        //     call.respond(repos.issueRepository.getAllIssues())
        // }

        post {
            val issue = call.receive<IssueEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val project = repos.projectRepository.getProjectByID(issue.projectID ?: 0)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
                return@post
            }

            var user = project.collaborators.find { user -> user.id == userId }

            if (project.owner.id != userId &&
                user == null) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this project")
                return@post
            }

            val created = repos.issueRepository.createIssue(issue)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create issue")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)

            if (user != null && project.owner.notificationsActive) {
                val to = project.owner.emailAddress
                val subject = "${project.name}: ${user.username} created a new issue"
                val body = "<h3>${issue.title}</h3><p>${issue.description}</p>"

                val response = mailer.sendEmail(
                    to,
                    subject,
                    body,
                )

                println("EMAIL: $to -> ${response.status}")
                if (response.status != HttpStatusCode.OK) {
                    println(response.body<String>())
                }
            }
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

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val project = repos.projectRepository.getProjectByID(issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Issue's project not found")
                return@get
            }

            if (project.owner.id != userId &&
                project.collaborators.find { user -> user.id == userId } == null) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
                return@get
            }

            call.respond(issue)
        }

        put {
            val entry = call.receive<IssueEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val issue = repos.issueRepository.getIssueByID(entry.id ?: 0)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@put
            }

            val project = repos.projectRepository.getProjectByID(issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Issue's project not found")
                return@put
            }

            if (project.owner.id != userId &&
                issue.createdBy.id != userId) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
                return@put
            }

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

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val issue = repos.issueRepository.getIssueByID(id)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@delete
            }

            val project = repos.projectRepository.getProjectByID(issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Issue's project not found")
                return@delete
            }

            if (project.owner.id != userId &&
                issue.createdBy.id != userId) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
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

fun Route.linkRoutes(repos: Repositories, mailer: Mailer) {
    route("/links") {
        post {
            val link = call.receive<IssueLinkEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val linker_issue = repos.issueRepository.getIssueByID(link.linker)
            if (linker_issue == null) {
                call.respond(HttpStatusCode.NotFound, "Linker issue not found")
                return@post
            }

            val linked_issue = repos.issueRepository.getIssueByID(link.linked)
            if (linked_issue == null) {
                call.respond(HttpStatusCode.NotFound, "Linked issue not found")
                return@post
            }

            val project = repos.projectRepository.getProjectByID(linker_issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Linker issue's project not found")
                return@post
            }

            if (project.owner.id != userId &&
                project.collaborators.find { user -> user.id == userId } == null) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
                return@post
            }

            if (linker_issue.project.id != linked_issue.project.id) {
                call.respond(HttpStatusCode.BadRequest, "Only issues under the same project can be linked")
                return@post
            }

            val created = repos.issueRepository.linkIssues(link)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create link")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        delete {
            val link = call.receive<IssueLinkEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val issue = repos.issueRepository.getIssueByID(link.linker)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Linker issue not found")
                return@delete
            }

            val project = repos.projectRepository.getProjectByID(issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Linker issue's project not found")
                return@delete
            }

            if (project.owner.id != userId &&
                issue.createdBy.id != userId) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
                return@delete
            }

            if (!repos.issueRepository.deleteLink(link)) {
                call.respond(HttpStatusCode.NotFound, "Unable to remove link")
                return@delete
            }

            call.respond("Removed")
        }
    }
}

fun Route.commentRoutes(repos: Repositories, mailer: Mailer) {
    route("/comments") {
        post {
            val comment = call.receive<CommentEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val issue = repos.issueRepository.getIssueByID(comment.issueID ?: 0)
            if (issue == null) {
                call.respond(HttpStatusCode.NotFound, "Issue not found")
                return@post
            }

            val project = repos.projectRepository.getProjectByID(issue.project.id)
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Issue's project not found")
                return@post
            }

            if (project.owner.id != userId &&
                project.collaborators.find { user -> user.id == userId } == null) {

                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this issue")
                return@post
            }

            val created = repos.commentRepository.createComment(comment)
            if (created == null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create comment")
                return@post
            }

            call.respond(HttpStatusCode.Created, created)
        }

        put {
            val entry = call.receive<CommentEntryRequest>()

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val comment = repos.commentRepository.getCommentByID(entry.id ?: 0)
            if (comment == null) {
                call.respond(HttpStatusCode.NotFound, "Comment not found")
                return@put
            }

            if (comment.author.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this comment")
                return@put
            }

            val edited = repos.commentRepository.editComment(entry)
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

            val principal = call.principal<JWTPrincipal>()
            val userId = principal!!.payload.getClaim("userId").asInt()

            val comment = repos.commentRepository.getCommentByID(commentID)
            if (comment == null) {
                call.respond(HttpStatusCode.NotFound, "Comment not found")
                return@delete
            }

            // TODO: technically also the project owner and issue owner will be able to delete the comment
            if (comment.author.id != userId) {
                call.respond(HttpStatusCode.Unauthorized, "Not authorized to access this comment")
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

fun Route.userRoutes(repos: Repositories, mailer: Mailer) {
    route("/users") {
        // get {
        //     call.respond(repos.userRepository.getAllUsers())
        // }

        // post {
        // }

        // get("/{id}") {
        //     val uid = call.parameters["id"]?.toIntOrNull()
        //     if (uid == null) {
        //         call.respond(HttpStatusCode.BadRequest, "ID must be a number")
        //         return@get
        //     }

        //     val user = repos.userRepository.getUserByID(uid)
        //     if (user == null) {
        //         call.respond(HttpStatusCode.NotFound, "User not found")
        //         return@get
        //     }

        //     call.respond(user)
        // }

        // Ping endpoint to check if the user's JWT is still valid
        get("/me") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()

            val user = repos.userRepository.getUserByUsername(username)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@get
            }

            call.respond(user)
        }

        get("/{username}") {
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
