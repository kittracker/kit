package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.orm.entries.IssueEntry
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest

interface IssueRepository {
    suspend fun createIssue(issue: IssueEntryRequest): Issue?
    suspend fun getIssuesByProjectID(id: Int): List<Issue>
    suspend fun getIssueByID(id: Int): Issue?
    suspend fun getIssueLinks(id: Int): List<IssueLink>
    suspend fun getAllIssues() : List<Issue>
    suspend fun editIssue(issue: IssueEntryRequest): Issue?
    suspend fun deleteIssue(id: Int): Boolean
    suspend fun linkIssues(link: IssueLinkEntryRequest): IssueLink?
    suspend fun deleteLink(link: IssueLinkEntryRequest): Boolean
}