package edu.kitt.orm.entries

import com.typesafe.config.Optional
import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntry(
    @Optional var id: Int? = null,
    val title: String,
    val description: String,
    @Optional val status: IssueStatus = IssueStatus.OPEN,
    val createdBy: Int,
    val projectID: Int,
)
