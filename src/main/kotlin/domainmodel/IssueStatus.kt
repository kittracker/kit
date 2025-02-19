package edu.kitt.domainmodel

import kotlinx.serialization.Serializable

@Serializable
enum class IssueStatus { OPEN, IN_PROGRESS, CLOSED }