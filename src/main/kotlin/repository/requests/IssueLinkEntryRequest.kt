package edu.kitt.repository.requests

import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntryRequest(
    val linker: Int,
    val linked: Int,
)
