package edu.kitt.repository.entries

import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntry(
    val id: Int,
    val title: String,
    val description: String,
    val status: IssueStatus = IssueStatus.OPEN,
    val createdBy: Int,
    val projectID: Int,
)
