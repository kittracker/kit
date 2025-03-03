package edu.kitt.orm.entries

import kotlinx.serialization.Serializable

@Serializable
data class UserEntry(
    val id: Int,
    val emailAddress: String,
    val username: String
)
