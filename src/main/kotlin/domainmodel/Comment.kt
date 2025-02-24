package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

// TODO: maybe change User with Int user -> userId
// TODO: maybe issueID is redundant
@Serializable
data class Comment(val id: Int, val author: User, val text: String, val issueID: Int)
