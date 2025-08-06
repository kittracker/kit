package edu.kitt

import edu.kitt.domainmodel.IssueStatus
import edu.kitt.orm.CommentRepository
import edu.kitt.orm.exposed.ExposedCommentRepository
import edu.kitt.orm.exposed.ExposedIssueRepository
import edu.kitt.orm.exposed.ExposedProjectRepository
import edu.kitt.orm.exposed.ExposedUserRepository
import edu.kitt.orm.IssueRepository
import edu.kitt.orm.ProjectRepository
import edu.kitt.orm.UserRepository
import edu.kitt.orm.exposed.Collaborators
import edu.kitt.orm.exposed.Comments
import edu.kitt.orm.exposed.IssueLinks
import edu.kitt.orm.exposed.Issues
import edu.kitt.orm.exposed.Projects
import edu.kitt.orm.exposed.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.addLogger
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction


fun Application.configureDatabases(useEmbedded: Boolean = false) {
    if (useEmbedded) {
        log.info("Using embedded H2 database")
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "root", password = "")
    } else {
        val url = environment.config.property("postgres.url").getString()
        log.info("Connecting to postgres database at $url")
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()
        Database.connect(
            url,
            user = user,
            password = password
        )
    }
}

class Repositories(
    var userRepository: UserRepository,
    var commentRepository: CommentRepository,
    var issueRepository: IssueRepository,
    var projectRepository: ProjectRepository
)

// TODO: since exposed has its own in-memory system, why we should maintain this?
//fun Application.initInMemoryRepositories(): Repositories {
//    val userRepository = InMemoryUserRepository()
//    val commentRepository = InMemoryCommentRepository(userRepository)
//    val issueRepository = InMemoryIssueRepository(commentRepository, userRepository)
//    val projectRepository = InMemoryProjectRepository(userRepository, issueRepository)
//
//    return Repositories(
//        userRepository = userRepository,
//        commentRepository = commentRepository,
//        issueRepository = issueRepository,
//        projectRepository = projectRepository
//    )
//}

fun Application.initExposedRepositories(): Repositories {
    val userRepository = ExposedUserRepository()
    val commentRepository = ExposedCommentRepository()
    val issueRepository = ExposedIssueRepository()
    val projectRepository = ExposedProjectRepository()

    return Repositories(
        userRepository = userRepository,
        commentRepository = commentRepository,
        issueRepository = issueRepository,
        projectRepository = projectRepository
    )
}

fun seedDatabase() {
    transaction {
        addLogger(StdOutSqlLogger)

        // Drop all tables in order of dependency
        SchemaUtils.drop(Collaborators, Comments, IssueLinks, Issues, Projects, Users)

        // Recreate the schema
        SchemaUtils.create(Users, Projects, Issues, IssueLinks, Comments, Collaborators)

        Users.insert { user ->
            user[userName] = "spectrev333"
            user[emailAddress] = "spectrev333@kittracker.org"
            user[passwordHash] = hashPassword("spectrev333")
        }

        Users.insert { user ->
            user[userName] = "cardisk"
            user[emailAddress] = "cardisk@kittracker.org"
            user[passwordHash] = hashPassword("cardisk")
        }

        Users.insert { user ->
            user[userName] = "mircocaneschi"
            user[emailAddress] = "mircocaneschi@kittracker.org"
            user[passwordHash] = hashPassword("mircocaneschi")
        }

        Projects.insert { project ->
            project[name] = "Project 1"
            project[description] = "Description for project 1"
            project[archived] = false
            project[ownerID] = 1
        }

        Projects.insert { project ->
            project[name] = "Project 2"
            project[description] = "Description for project 2"
            project[archived] = false
            project[ownerID] = 2
        }

        Projects.insert { project ->
            project[name] = "Project 3"
            project[description] = "Description for project 3"
            project[archived] = false
            project[ownerID] = 3
        }

        Issues.insert { issue ->
            issue[title] = "Issue 1 for project 1"
            issue[description] = "Description for issue 1"
            issue[status] = IssueStatus.OPEN
            issue[owner] = 1
            issue[projectID] = 1
        }

        Issues.insert { issue ->
            issue[title] = "Issue 2 for project 2"
            issue[description] = "Description for issue 2"
            issue[status] = IssueStatus.OPEN
            issue[owner] = 1
            issue[projectID] = 2
        }

        Issues.insert { issue ->
            issue[title] = "Issue 3 for project 3"
            issue[description] = "Description for issue 3"
            issue[status] = IssueStatus.OPEN
            issue[owner] = 3
            issue[projectID] = 3
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 2
            collaborator[projectID] = 1
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 3
            collaborator[projectID] = 1
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 1
            collaborator[projectID] = 2
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 3
            collaborator[projectID] = 2
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 1
            collaborator[projectID] = 3
        }

        Collaborators.insert { collaborator ->
            collaborator[userID] = 2
            collaborator[projectID] = 3
        }
    }
}