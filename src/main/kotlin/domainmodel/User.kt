package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val emailAddress: String, val username: String)
