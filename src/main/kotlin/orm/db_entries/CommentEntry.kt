package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class CommentEntry(
    val id: Int,
    val author: Int,
    val text: String,
    val issueID: Int,
)

