package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

// TODO: maybe change User with Int user -> userId
// TODO: maybe projectID is redundant
@Serializable
data class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val status: IssueStatus,
    val createdBy: User,
    val comments: MutableList<Comment>,
    val links: MutableList<Issue>,
    val projectID: Int
);
