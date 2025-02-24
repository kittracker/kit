package edu.kitt

import edu.kitt.orm.InMemoryUserRepository
import edu.kitt.orm.UserRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*

val userRepository: UserRepository = InMemoryUserRepository()

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

        get("/") {
            call.respondText("Hello World!")
        }
    }

}
