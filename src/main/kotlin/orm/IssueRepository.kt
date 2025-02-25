package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink

interface IssueRepository {
    fun getIssuesByProjectID(id: Int): List<Issue>
    fun getIssueByID(id: Int): Issue?
    fun getIssueLinks(id: Int): List<IssueLink>
    fun getAllIssues() : List<Issue>
}