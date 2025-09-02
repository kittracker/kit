package edu.kitt.repository.requests

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val username: String,
    val password: String
)
