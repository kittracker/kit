package edu.kitt

import edu.kitt.repository.CommentRepository
import edu.kitt.repository.ExposedCommentRepository
import edu.kitt.repository.ExposedIssueRepository
import edu.kitt.repository.ExposedProjectRepository
import edu.kitt.repository.ExposedUserRepository
import edu.kitt.repository.IssueRepository
import edu.kitt.repository.ProjectRepository
import edu.kitt.repository.UserRepository
import io.ktor.server.application.*
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