package edu.kitt.orm.entries

import com.typesafe.config.Optional
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
