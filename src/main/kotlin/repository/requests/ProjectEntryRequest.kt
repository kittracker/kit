package edu.kitt.repository.requests

import kotlinx.serialization.Serializable

@Serializable
data class ProjectEntryRequest(
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val archived: Boolean? = null,
    val ownerID: Int? = null
)