package edu.kitt

import edu.kitt.domainmodel.IssueStatus
import edu.kitt.repository.requests.IssueEntryRequest
import edu.kitt.repository.requests.SignupRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IssueRoutesTest {
    @Test
    fun `test create issue as project collaborator`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" (id=2) is a collaborator on Project 1
        client.loginAs("cardisk", "cardisk")

        val response = client.post("/api/issues") {
            contentType(ContentType.Application.Json)
            setBody(
                IssueEntryRequest(
                    title = "New Issue by Collaborator",
                    description = "A test issue",
                    status = IssueStatus.OPEN,
                    createdBy = 2, // cardisk's ID
                    projectID = 1  // Project 1
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("New Issue by Collaborator"))
    }

    @Test
    fun `test create issue unauthorized`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // Register a new user who has no project access
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("unauthorizedUser", "unauthorized@user.com", "password"))
        }

        val response = client.post("/api/issues") {
            contentType(ContentType.Application.Json)
            setBody(
                IssueEntryRequest(
                    title = "Malicious Issue",
                    description = "",
                    status = IssueStatus.OPEN,
                    createdBy = 4, // New user's ID
                    projectID = 1
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test get issue as project owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" is the owner of Project 1, which contains Issue 1
        client.loginAs("spectrev333", "spectrev333")

        val response = client.get("/api/issues/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Issue 1 for project 1"))
    }

    @Test
    fun `test edit issue as issue creator`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) created Issue 2 in Project 2
        client.loginAs("spectrev333", "spectrev333")

        val response = client.put("/api/issues") {
            contentType(ContentType.Application.Json)
            setBody(
                IssueEntryRequest(
                    id = 2,
                    title = "Issue 2 Edited by Creator",
                    description = "Updated description",
                    status = IssueStatus.CLOSED,
                    createdBy = 1,
                    projectID = 2
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Issue 2 Edited by Creator"))
        assertTrue(body.contains("CLOSED"))
    }

    @Test
    fun `test edit issue as project owner (not creator)`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" (id=2) owns Project 2, where Issue 2 was created by "spectrev333"
        client.loginAs("cardisk", "cardisk")

        val response = client.put("/api/issues") {
            contentType(ContentType.Application.Json)
            setBody(IssueEntryRequest(id = 2, title = "Issue 2 Edited by Owner", status = IssueStatus.IN_PROGRESS))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Issue 2 Edited by Owner"))
    }

    @Test
    fun `test edit issue unauthorized as collaborator`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "mircocaneschi" is a collaborator on Project 2, but not the owner or issue creator
        client.loginAs("mircocaneschi", "mircocaneschi")

        val response = client.put("/api/issues") {
            contentType(ContentType.Application.Json)
            setBody(IssueEntryRequest(id = 2, title = "Malicious Edit", status = IssueStatus.OPEN))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test delete issue as project owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" owns Project 2, which contains Issue 2
        client.loginAs("cardisk", "cardisk")

        val deleteResponse = client.delete("/api/issues/2")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // Verify it's gone
        val getResponse = client.get("/api/issues/2")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }
}