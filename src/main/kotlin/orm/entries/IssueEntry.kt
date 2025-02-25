package edu.kitt.orm.entries

import edu.kitt.domainmodel.IssueStatus

data class IssueEntry(
    val id: Int,
    val title: String,
    val description: String,
    val status: IssueStatus,
    val createdBy: Int,
    val projectID: Int,
);
