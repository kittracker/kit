package edu.kitt.orm.entries

import kotlinx.serialization.Serializable

@Serializable
data class CollaboratorEntry(
    val userID: Int,
    val projectID: Int,
)