package edu.kitt.orm.entries

import kotlinx.serialization.Serializable

@Serializable
data class IssueLinkEntry(
    val linker: Int,
    val linked: Int,
)
