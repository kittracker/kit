package edu.kitt.orm.entries

import com.typesafe.config.Optional
import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntry(
    val id: UInt,
    val title: String,
    val description: String,
    val status: IssueStatus = IssueStatus.OPEN,
    val createdBy: UInt,
    val projectID: UInt,
)
