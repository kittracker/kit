package edu.kitt.repository.entries

import kotlinx.serialization.Serializable

@Serializable
data class ProjectEntry(
    val id: Int,
    val name: String,
    val description: String,
    val archived: Boolean,
    val ownerID: Int,
)