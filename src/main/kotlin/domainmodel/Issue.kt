package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class ProjectSummary(
    val id: Int,
    val name: String,
    val owner: User
)

@Serializable
data class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val status: IssueStatus,
    val project: ProjectSummary,
    val createdBy: User,
    val comments: MutableList<Comment>,
    val links: MutableList<IssueLink>
)
