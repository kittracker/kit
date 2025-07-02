package edu.kitt

import edu.kitt.authentication.JwtConfig
import edu.kitt.orm.*
import edu.kitt.orm.requests.LoginRequest
import edu.kitt.orm.requests.SignupRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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

    val jwtConfig: JwtConfig = JwtConfig(
        jwtIssuer = environment.config.property("jwt.issuer").getString(),
        jwtAudience = environment.config.property("jwt.audience").getString(),
        jwtRealm = environment.config.property("jwt.realm").getString(),
        jwtSecret = environment.config.property("jwt.secret").getString()
    )

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }
    authentication {
        jwt {
            jwtConfig.configureKtorFeature(this)
        }
    }

    configureDatabases(embedded = true)

    transaction {
        addLogger(StdOutSqlLogger)
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
    }

    val repos = initExposedRepositories()

    routing {
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val user = repos.userRepository.getUser(loginRequest.username, loginRequest.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
                return@post
            }

            val token = jwtConfig.generateToken(user.id, user.username)

            call.respond(hashMapOf("token" to token))
        }

        post("/register") {
            val signupRequest = call.receive<SignupRequest>()
            val user = repos.userRepository.createUser(signupRequest)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
                return@post
            }
            call.respond(user)
        }

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
