package edu.kitt.orm.entries

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class CommentEntry(
    @Optional var id: Int? = null,
    val author: Int,
    var text: String,
    val issueID: Int,
)

