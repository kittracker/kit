package edu.kitt

import edu.kitt.orm.requests.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.assertEquals

suspend fun HttpClient.loginAs(username: String, password: String) {
    val response = this.post("/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(username, password))
    }
    assertEquals(HttpStatusCode.OK, response.status, "Login failed for $username")
}

fun testApplicationWithConfig(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
    environment {
        config = ApplicationConfig("application.yaml")
    }
    block()
}