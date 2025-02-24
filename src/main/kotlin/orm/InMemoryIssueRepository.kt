package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.domainmodel.User

class InMemoryIssueRepository : IssueRepository {
    private val users = listOf<User>(
        User(1, "matteo@gmail.com", "cardisk"),
        User(2, "leonardo@gmail.com", "spectrev333"),
        User(3, "mirco@gmail.com", "mircocaneschi"),
    )

    private val issues = listOf<Issue>(
        Issue(1, "title", "description", IssueStatus.OPEN, users[0], mutableListOf(), mutableListOf(), 1),
        Issue(2, "title", "description", IssueStatus.OPEN, users[1], mutableListOf(), mutableListOf(), 1),
        Issue(3, "title", "description", IssueStatus.OPEN, users[2], mutableListOf(), mutableListOf(), 1),
    )

    override fun getIssuesByProjectID(id: Int): List<Issue> {
        return issues.filter { it.projectID == id }
    }

    override fun getIssues() : List<Issue> {
        return issues
    }
}