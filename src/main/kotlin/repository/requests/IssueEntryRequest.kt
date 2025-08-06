package edu.kitt.orm.requests

import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntryRequest(
    var id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val status: IssueStatus? = null,
    val owner: Int? = null,
    val projectID: Int? = null,
)
