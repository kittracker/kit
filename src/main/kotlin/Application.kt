package edu.kitt

import edu.kitt.orm.*
import edu.kitt.orm.inmemory.InMemoryCommentRepository
import edu.kitt.orm.inmemory.InMemoryIssueRepository
import edu.kitt.orm.inmemory.InMemoryProjectRepository
import edu.kitt.orm.inmemory.InMemoryUserRepository
import edu.kitt.orm.sql.SqlUserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import java.io.File

class Repositories(
    var userRepository: UserRepository,
    var commentRepository: CommentRepository,
    var issueRepository: IssueRepository,
    var projectRepository: ProjectRepository
)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.initInMemoryRepositories(): Repositories {

    val connection = connectToPostgres(embedded = false)

    val userRepository = SqlUserRepository(connection)
    val commentRepository = InMemoryCommentRepository(userRepository)
    val issueRepository = InMemoryIssueRepository(commentRepository, userRepository)
    val projectRepository = InMemoryProjectRepository(userRepository, issueRepository)

    return Repositories(
        userRepository = userRepository,
        commentRepository = commentRepository,
        issueRepository = issueRepository,
        projectRepository = projectRepository
    )
}

fun Application.module() {

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    val dbConnection = connectToPostgres(embedded = true)
    val repos = initInMemoryRepositories()

    routing {
        route("/api") {
            projectRoutes(repos)
            collaboratorRoutes(repos)

            issueRoutes(repos)
            commentRoutes(repos)
            linkRoutes(repos)

            userRoutes(repos)
        }

        // TODO: switch these lines at the end of development
        // staticFiles("/", File("src/main/resources/static"))
        // staticResources("/", "static")

        staticFiles(
            "/",
            File("src/main/resources/static")
        )

        //singlePageApplication {
        //    filesPath = "static"
        //    defaultPage = "index.html"
        //    useResources = true
        //}

        // get("/") {
        //     call.respondText("Hello World!")
        // }
    }

}
