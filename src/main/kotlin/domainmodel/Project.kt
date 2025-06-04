package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val archived: Boolean,
    val owner: User,
    val collaborators: MutableList<User>,
    val issues: MutableList<Issue>
)
