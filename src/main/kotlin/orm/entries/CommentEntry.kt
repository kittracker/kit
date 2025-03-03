package edu.kitt.orm.entries

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntry(
    var id: Int,
    val author: Int,
    val text: String,
    val issueID: Int,
)

