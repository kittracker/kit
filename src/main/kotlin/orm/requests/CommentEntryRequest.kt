package edu.kitt.orm.requests

import com.typesafe.config.Optional
import kotlinx.serialization.Serializable

@Serializable
data class CommentEntryRequest(
    var id: UInt? = null,
    val author: UInt? = null, // TODO: maybe remove when auth is implemented
    var text: String? = null,
    val issueID: UInt? = null,
)

