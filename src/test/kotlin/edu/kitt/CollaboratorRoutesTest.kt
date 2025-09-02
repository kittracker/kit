package edu.kitt

import CollaboratorEntryRequest
import edu.kitt.domainmodel.Project
import edu.kitt.repository.requests.ProjectEntryRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollaboratorRoutesTest {

    private suspend fun HttpClient.createProject(req: ProjectEntryRequest) =
        post("/api/projects") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }

    @Test
    fun `test add collaborator as project owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) owns Project 1. Let's add a new user to it.
        client.loginAs("spectrev333", "spectrev333")

        val projectResponse = client.createProject(ProjectEntryRequest(
            name = "New Test Project",
            description = "A project created from a test",
            archived = false,
            ownerID = 1
        ))

        assertEquals(HttpStatusCode.Created, projectResponse.status)
        val project = projectResponse.body<Project>()

        // User "mircocaneschi" (id=3) is not on Project 1 initially.
        val response = client.post("/api/collaborators") {
            contentType(ContentType.Application.Json)
            setBody(CollaboratorEntryRequest(userID = 3, projectID = project.id))
        }

        assertEquals(HttpStatusCode.Created, response.status)

        // Verify by having the new collaborator access the project
        client.loginAs("mircocaneschi", "mircocaneschi")
        val projectGetResponse = client.get("/api/projects/1")
        assertEquals(HttpStatusCode.OK, projectGetResponse.status)
        assertTrue(projectGetResponse.bodyAsText().contains("Project 1"))
    }

    @Test
    fun `test add collaborator unauthorized as non-owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" (id=2) is a collaborator on Project 1, but not the owner.
        client.loginAs("cardisk", "cardisk")

        // They should not be able to add another user.
        val response = client.post("/api/collaborators") {
            contentType(ContentType.Application.Json)
            setBody(CollaboratorEntryRequest(userID = 3, projectID = 1))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}