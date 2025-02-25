package edu.kitt.orm.entries

data class CommentEntry(
    val id: Int,
    val author: Int,
    val text: String,
    val issueID: Int,
)

