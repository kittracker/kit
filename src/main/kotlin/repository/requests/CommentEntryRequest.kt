package edu.kitt.repository.requests

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntryRequest(
    var id: Int? = null,
    val author: Int? = null, // TODO: maybe remove when auth is implemented
    var text: String? = null,
    val issueID: Int? = null,
)

