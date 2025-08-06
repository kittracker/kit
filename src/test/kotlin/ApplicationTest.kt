package edu.kitt

import edu.kitt.orm.requests.LoginRequest
import edu.kitt.orm.requests.SignupRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue



class ApplicationTest {

    @Test
    fun testRoot() = testApplicationWithConfig {
        application { module() }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test successful login`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            // Using the data class is cleaner than a raw JSON string
            setBody(LoginRequest("spectrev333", "spectrev333"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val setCookieHeader = response.headers[HttpHeaders.SetCookie]
        assertNotNull(setCookieHeader)
        assertTrue(setCookieHeader.contains("jwt-token"))
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("spectrev333"))
    }

    @Test
    fun `test failed login with wrong password`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("spectrev333", "wrongpassword"))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals("Invalid credentials.", response.bodyAsText())
    }

    @Test
    fun `test successful registration`() = testApplicationWithConfig {
        application { module() }
        val client = createClient { install(ContentNegotiation) { json() } }

        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("newuser", "newuser@test.com", "password123"))
        }

        // The registration endpoint in Application.kt returns OK, not Created.
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("newuser"))
    }

    @Test
    fun `test successful logout`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        // 1. First, log in to establish a session and get a cookie.
        client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("spectrev333", "spectrev333"))
        }

        // 2. Now, call the logout endpoint.
        val logoutResponse = client.post("/logout")

        assertEquals(HttpStatusCode.OK, logoutResponse.status)
        val setCookieHeader = logoutResponse.headers[HttpHeaders.SetCookie]
        assertNotNull(setCookieHeader, "Set-Cookie header should be present on logout")
        assertTrue(setCookieHeader.contains("Max-Age=0"), "Cookie should be expired on logout")
    }

    @Test
    fun `test get me endpoint when authenticated`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        client.loginAs("cardisk", "cardisk")

        val response = client.get("/api/users/me")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"username\":\"cardisk\""))
        assertTrue(body.contains("\"id\":2"))
    }

    @Test
    fun `test get user by username`() = testApplicationWithConfig {
        application { module() }
        val response = client.get("/api/users/mircocaneschi")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"username\":\"mircocaneschi\""))
    }
}
