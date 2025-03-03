package edu.kitt.orm.entries

import com.typesafe.config.Optional
import edu.kitt.domainmodel.IssueStatus

import kotlinx.serialization.Serializable

@Serializable
data class IssueEntry(
    @Optional val id: Int? = null,
    val title: String,
    val description: String,
    val status: IssueStatus,
    val createdBy: Int,
    val projectID: Int,
);
