package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: UInt,
    val author: User,
    val text: String
)
