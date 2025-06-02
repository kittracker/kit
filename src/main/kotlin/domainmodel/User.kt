package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: UInt,
    val emailAddress: String,
    val username: String
)
