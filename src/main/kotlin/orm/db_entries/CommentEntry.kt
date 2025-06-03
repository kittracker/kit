package edu.kitt.orm.entries

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntry(
    val id: UInt,
    val author: UInt,
    val text: String,
    val issueID: UInt,
)

