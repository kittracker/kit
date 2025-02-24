package edu.kitt.orm

import edu.kitt.domainmodel.Issue

interface IssueRepository {
    fun getIssuesByProjectID(id: Int): List<Issue>
    fun getIssues() : List<Issue>
}