package edu.kitt

import edu.kitt.orm.requests.CommentEntryRequest
import edu.kitt.orm.requests.SignupRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommentRoutesTest {

    /**
     * A helper to create a comment and return its ID, reducing test boilerplate.
     */
    private suspend fun HttpClient.createComment(issueId: Int, author: Int, text: String): Int {
        val response = this.post("/api/comments") {
            contentType(ContentType.Application.Json)
            setBody(CommentEntryRequest(text = text, author = author, issueID = issueId))
        }
        assertEquals(HttpStatusCode.Created, response.status, "Failed to create comment for test setup")
        val body = response.bodyAsText()
        // A simple way to parse the ID from the JSON response
        return Json.parseToJsonElement(body).jsonObject["id"]!!.jsonPrimitive.content.toInt()
    }

    @Test
    fun `test create comment as project collaborator`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" (id=2) is a collaborator on Project 1, which contains Issue 1
        client.loginAs("cardisk", "cardisk")
        client.createComment(issueId = 1, author = 2, text = "This is a test comment")
    }

    @Test
    fun `test create comment unauthorized`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // Register a new user who is not part of any project
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("noAccessUser", "no@access.com", "password"))
        }

        // Try to comment on Issue 1
        val response = client.post("/api/comments") {
            contentType(ContentType.Application.Json)
            setBody(CommentEntryRequest(text = "malicious comment", author = 4, issueID = 1))
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test edit comment as author`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) is on Project 2, which has Issue 2
        client.loginAs("spectrev333", "spectrev333")
        val commentId = client.createComment(issueId = 2, author = 1, text = "Original comment")

        val response = client.put("/api/comments") {
            contentType(ContentType.Application.Json)
            setBody(CommentEntryRequest(id = commentId, text = "Edited comment"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Edited comment"))
    }

    @Test
    fun `test edit comment unauthorized as non-author`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // User 1 ("spectrev333") creates a comment
        client.loginAs("spectrev333", "spectrev333")
        val commentId = client.createComment(issueId = 2, author = 1, text = "A comment")

        // User 2 ("cardisk") tries to edit it
        client.loginAs("cardisk", "cardisk")
        val response = client.put("/api/comments") {
            contentType(ContentType.Application.Json)
            setBody(CommentEntryRequest(id = commentId, text = "Malicious edit"))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test delete comment as author`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        client.loginAs("spectrev333", "spectrev333")
        val commentId = client.createComment(issueId = 1, author = 1, text = "To be deleted")

        val response = client.delete("/api/comments/$commentId")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test delete comment unauthorized as non-author`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // User 1 creates a comment
        client.loginAs("spectrev333", "spectrev333")
        val commentId = client.createComment(issueId = 1, author = 1, text = "A comment")

        // User 2 tries to delete it
        client.loginAs("cardisk", "cardisk")
        val response = client.delete("/api/comments/$commentId")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}