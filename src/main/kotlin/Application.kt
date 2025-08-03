package edu.kitt

import edu.kitt.authentication.JwtConfig
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.orm.*
import edu.kitt.orm.requests.LoginRequest
import edu.kitt.orm.requests.SignupRequest
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.addLogger
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    val jwtConfig = JwtConfig(
        jwtIssuer = environment.config.property("jwt.issuer").getString(),
        jwtAudience = environment.config.property("jwt.audience").getString(),
        jwtRealm = environment.config.property("jwt.realm").getString(),
        jwtSecret = environment.config.property("jwt.secret").getString()
    )

    install(CallLogging)
    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            if (cause is IllegalArgumentException) {
                call.respondText(text = "400: ${cause.message}" , status = HttpStatusCode.BadRequest)
            } else {
                call.respondText(text = "500: ${cause.message}" , status = HttpStatusCode.InternalServerError)
            }
        }
    }

    authentication {
        jwt("auth-jwt") {
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
            issue[createdBy] = 1
            issue[projectID] = 1
        }

        Issues.insert { issue ->
            issue[title] = "Issue 2 for project 2"
            issue[description] = "Description for issue 2"
            issue[status] = IssueStatus.OPEN
            issue[createdBy] = 1
            issue[projectID] = 2
        }

        Issues.insert { issue ->
            issue[title] = "Issue 3 for project 3"
            issue[description] = "Description for issue 3"
            issue[status] = IssueStatus.OPEN
            issue[createdBy] = 3
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

    val repos = initExposedRepositories()

    val maxAge = 24 * 60 * 60 // max age for tokens

    routing {
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val user = repos.userRepository.getUser(loginRequest.username, loginRequest.password)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
                return@post
            }

            val token = jwtConfig.generateToken(user.id, user.username, maxAge)

            val cookie = Cookie(
                name = "jwt-token",
                value = token,
                maxAge = maxAge,
                httpOnly = true,
                // TODO: in production change this line
                // secure = true,
                secure = false,
                path = "/",
                extensions = mapOf("SameSite" to SameSite.Strict),
            )

            call.response.cookies.append(cookie)

            call.respond(HttpStatusCode.OK, user)
        }

        post("/logout") {
            val cookie = Cookie(
                name = "jwt-token",
                value = "",
                maxAge = 0,
                httpOnly = true,
                // TODO: in production change this line
                // secure = true,
                secure = false,
                path = "/",
                extensions = mapOf("SameSite" to SameSite.Strict),
            )

            call.response.cookies.append(cookie)
            call.respond(HttpStatusCode.OK, "Logged out successfully.")
        }

        post("/register") {
            val signupRequest = call.receive<SignupRequest>()
            val user = repos.userRepository.createUser(signupRequest)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Registration failed.")
                return@post
            }

            val token = jwtConfig.generateToken(user.id, user.username, maxAge)

            val cookie = Cookie(
                name = "jwt-token",
                value = token,
                maxAge = maxAge,
                httpOnly = true,
                // TODO: in production change this line
                // secure = true,
                secure = false,
                path = "/",
                extensions = mapOf("SameSite" to SameSite.Strict),
            )

            call.response.cookies.append(cookie)

            call.respond(HttpStatusCode.OK, user)
        }

        authenticate("auth-jwt") {
            route("/api") {
                projectRoutes(repos)
                collaboratorRoutes(repos)

                issueRoutes(repos)
                commentRoutes(repos)
                linkRoutes(repos)

                userRoutes(repos)
            }
        }

        // TODO: uncomment this when production ready
        singlePageApplication {
            filesPath = "src/main/resources/static"
            // filesPath = "static"
            defaultPage = "index.html"
            // useResources = true
        }
    }

}
