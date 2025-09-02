package edu.kitt.repository.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserEntryRequest(
    val id: Int? = null,
    val emailAddress: String? = null,
    val username: String? = null,
)
