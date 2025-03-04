package edu.kitt

import edu.kitt.orm.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import java.io.File

val userRepository: UserRepository = InMemoryUserRepository()
val commentRepository: CommentRepository = InMemoryCommentRepository()
val issueRepository: IssueRepository = InMemoryIssueRepository()
val projectRepository: ProjectRepository = InMemoryProjectRepository()

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    routing {
        projectRoutes()
        userRoutes()
        issueRoutes()
        commentRoutes()

        // TODO: switch these lines at the end of development
        staticFiles("/", File("src/main/resources/static"))
        // staticResources("/", "static")

        // get("/") {
        //     call.respondText("Hello World!")
        // }
    }

}
