package edu.kitt.orm.requests

import com.typesafe.config.Optional
import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntryRequest(
    var id: UInt? = null,
    val title: String? = null,
    val description: String? = null,
    val status: IssueStatus? = null,
    val createdBy: UInt? = null,
    val projectID: UInt? = null,
)
