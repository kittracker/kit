package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class Issue(
    val id: UInt,
    val title: String,
    val description: String,
    val status: IssueStatus,
    val createdBy: User,
    val comments: MutableList<Comment>,
    val links: MutableList<IssueLink>
);
