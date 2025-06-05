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
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.addLogger
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    configureDatabases(embedded = true)

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Users, Projects, Issues, IssueLinks, Comments, Collaborators)

        Users.insert { user ->
            user[userName] = "spectrev333"
            user[emailAddress] = "spectrev333@kittracker.org"
        }

        Users.insert { user ->
            user[userName] = "cardisk"
            user[emailAddress] = "cardisk@kittracker.org"
        }

        Users.insert { user ->
            user[userName] = "mircocaneschi"
            user[emailAddress] = "mircocaneschi@kittracker.org"
        }
    }

    val repos = initExposedRepositories()

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
