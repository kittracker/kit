package edu.kitt

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import io.ktor.server.application.Application

data class Mailer(
    val client: HttpClient,
    val sender: String,
    val apiKey: String,
)

suspend fun Mailer.sendEmail(to: String, subject: String, body: String) : HttpResponse {
    val mailBody = "{\"sender\": \"$sender\", \"to\": [\"$to\"], \"subject\": \"$subject\", \"html_body\": \"$body\"}"
    return client.post("https://api.smtp2go.com/v3/email/send") {
        headers {
            append(HttpHeaders.ContentType, "application/json")
            append(HttpHeaders.Accept, "application/json")
            append("X-Smtp2go-Api-Key", apiKey)
        }
        setBody(mailBody)
    }
}

fun Application.setUpMailer() : Mailer {
    val sender = System.getenv("SMTP2GO_SENDER") ?: ""
    val apiKey = System.getenv("SMTP2GO_API_KEY") ?: ""

    return Mailer(
        client = HttpClient(CIO),
        sender = sender,
        apiKey = apiKey,
    )
}