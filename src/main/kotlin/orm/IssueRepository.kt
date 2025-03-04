package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.orm.entries.IssueEntry

interface IssueRepository {
    fun createIssue(issue: IssueEntry): Issue?
    fun getIssuesByProjectID(id: Int): List<Issue>
    fun getIssueByID(id: Int): Issue?
    fun getIssueLinks(id: Int): List<IssueLink>
    fun getAllIssues() : List<Issue>
    fun editIssue(issue: IssueEntry): Issue?
    fun deleteIssue(id: Int): Boolean
}