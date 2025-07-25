package edu.kitt

import edu.kitt.orm.CommentRepository
import edu.kitt.orm.ExposedCommentRepository
import edu.kitt.orm.ExposedIssueRepository
import edu.kitt.orm.ExposedProjectRepository
import edu.kitt.orm.ExposedUserRepository
import edu.kitt.orm.inmemory.InMemoryUserRepository
import edu.kitt.orm.IssueRepository
import edu.kitt.orm.ProjectRepository
import edu.kitt.orm.UserRepository
import edu.kitt.orm.inmemory.InMemoryCommentRepository
import edu.kitt.orm.inmemory.InMemoryIssueRepository
import edu.kitt.orm.inmemory.InMemoryProjectRepository
import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager
import org.jetbrains.exposed.v1.jdbc.Database


fun Application.configureDatabases(embedded: Boolean) {
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")
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