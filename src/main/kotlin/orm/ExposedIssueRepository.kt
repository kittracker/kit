package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest

class ExposedIssueRepository : IssueRepository {
    override suspend fun createIssue(issue: IssueEntryRequest): Issue? {
        TODO("Not yet implemented")
    }

    override suspend fun getIssuesByProjectID(id: Int): List<Issue> {
        TODO("Not yet implemented")
    }

    override suspend fun getIssueByID(id: Int): Issue? {
        TODO("Not yet implemented")
    }

    override suspend fun getIssueLinks(id: Int): List<IssueLink> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllIssues(): List<Issue> {
        TODO("Not yet implemented")
    }

    override suspend fun editIssue(issue: IssueEntryRequest): Issue? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteIssue(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun linkIssues(link: IssueLinkEntryRequest): IssueLink? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLink(link: IssueLinkEntryRequest): Boolean {
        TODO("Not yet implemented")
    }
}