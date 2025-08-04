package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class ProjectSummary(
    val id: Int,
    val name: String,
    val owner: User
)
