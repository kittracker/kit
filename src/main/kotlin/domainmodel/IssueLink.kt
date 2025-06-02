package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
data class IssueLink(
    val id: UInt,
    val title: String,
)
