package edu.kitt

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*

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

        get("/") {
            call.respondText("Hello World!")
        }
    }

}
