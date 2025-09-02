package edu.kitt.repository.entries

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntry(
    val id: Int,
    val author: Int,
    val text: String,
    val issueID: Int,
)

