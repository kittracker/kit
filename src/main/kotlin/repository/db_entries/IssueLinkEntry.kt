package edu.kitt.repository.entries

import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntry(
    val linker: Int,
    val linked: Int,
)
