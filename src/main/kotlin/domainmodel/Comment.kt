package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val author: User,
    val text: String
)
