package edu.kitt

import edu.kitt.orm.requests.LoginRequest
import edu.kitt.orm.requests.ProjectEntryRequest
import edu.kitt.orm.requests.SignupRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectRoutesTest {

    /**
     * A helper extension function to log in a user.
     * This makes tests that require authentication cleaner and avoids repetition.
     * It performs the login and asserts that it was successful.
     */

    @Test
    fun `test get projects when not authenticated`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        val response = client.get("/api/projects")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test get projects when authenticated`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        client.loginAs("spectrev333", "spectrev333")

        val projectsResponse = client.get("/api/projects")

        assertEquals(HttpStatusCode.OK, projectsResponse.status)
        val projectsBody = projectsResponse.bodyAsText()
        assertTrue(projectsBody.contains("Project 1"))
        assertTrue(projectsBody.contains("Project 2"))
        assertTrue(projectsBody.contains("Project 3"))
    }

    @Test
    fun `test get single project when authenticated`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)

        }

        client.loginAs("cardisk", "cardisk")

        val response = client.get("/api/projects/2")

        assertEquals(HttpStatusCode.OK, response.status)
        val projectBody = response.bodyAsText()
        assertTrue(projectBody.contains("\"name\":\"Project 2\""))
        assertTrue(projectBody.contains("\"id\":2"))
    }

    @Test
    fun `test get non-existent project`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)

        }
        client.loginAs("spectrev333", "spectrev333")

        val response = client.get("/api/projects/999") // A project ID that doesn't exist
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test create new project`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // User "spectrev333" has id 1 from the seeded data
        client.loginAs("spectrev333", "spectrev333")

        val response = client.post("/api/projects") {
            contentType(ContentType.Application.Json)
            setBody(
                ProjectEntryRequest(
                    name = "New Test Project",
                    description = "A project created from a test",
                    archived = false,
                    ownerID = 1
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("New Test Project"))

        // Verify the project was actually created by fetching it in the list
        val projectsResponse = client.get("/api/projects")
        assertTrue(projectsResponse.bodyAsText().contains("New Test Project"))
    }

    @Test
    fun `test get single project as collaborator`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) is a collaborator on Project 2, which is owned by "cardisk" (id=2)
        client.loginAs("spectrev333", "spectrev333")

        val response = client.get("/api/projects/2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Project 2"))
    }

    @Test
    fun `test get single project unauthorized`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(SignupRequest("newUser", "new@user.com", "password"))
        }

        val response = client.get("/api/projects/2")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test edit project successfully as owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "cardisk" (id=2) is the owner of project 2
        client.loginAs("cardisk", "cardisk")

        val response = client.put("/api/projects") {
            contentType(ContentType.Application.Json)
            setBody(
                ProjectEntryRequest(
                    id = 2,
                    name = "Project 2 Edited",
                    description = "This description was edited",
                    archived = false,
                    ownerID = 2
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Project 2 Edited"))
    }

    @Test
    fun `test edit project unauthorized as non-owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) is a collaborator, but not the owner of project 2
        client.loginAs("spectrev333", "spectrev333")

        val response = client.put("/api/projects") {
            contentType(ContentType.Application.Json)
            setBody(
                ProjectEntryRequest(
                    id = 2,
                    name = "Malicious Edit",
                    description = "",
                    archived = false,
                    ownerID = 2
                )
            )
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test delete project successfully as owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "mircocaneschi" (id=3) is the owner of project 3
        client.loginAs("mircocaneschi", "mircocaneschi")

        val deleteResponse = client.delete("/api/projects/3")
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // Verify it's gone by trying to fetch it again
        val getResponse = client.get("/api/projects/3")
        assertEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `test delete project unauthorized as non-owner`() = testApplicationWithConfig {
        application { module() }
        val client = createClient {
            install(ContentNegotiation) { json() }
            install(HttpCookies)
        }

        // "spectrev333" (id=1) is not the owner of project 2
        client.loginAs("spectrev333", "spectrev333")

        val response = client.delete("/api/projects/2")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}