package edu.kitt.orm

import edu.kitt.commentRepository
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.issueRepository
import edu.kitt.orm.entries.IssueEntry
import edu.kitt.orm.entries.IssueLinkEntry
import edu.kitt.userRepository

class InMemoryIssueRepository : IssueRepository {
    private val issueLinks = listOf(
        IssueLinkEntry(1, 2),
        IssueLinkEntry(2, 1),
        IssueLinkEntry(3, 1),
    )

    private val issues = listOf(
        IssueEntry(1, "title", "description", IssueStatus.OPEN, 1, 1),
        IssueEntry(2, "title", "description", IssueStatus.OPEN, 2, 1),
        IssueEntry(3, "title", "description", IssueStatus.OPEN, 3, 1),
    )

    override fun getIssuesByProjectID(id: Int): List<Issue> {
        return issues.filter { it.projectID == id }.map {
            // FIXME: this can throw
            issueRepository.getIssueByID(it.id)!!
        }
    }

    override fun getIssueByID(id: Int): Issue? {
        val issueEntry = issues.firstOrNull { it.id == id }
        if (issueEntry == null) return null
        return Issue(
            issueEntry.id,
            issueEntry.title,
            issueEntry.description,
            issueEntry.status,
            // FIXME: this can throw
            userRepository.getUserByID(issueEntry.createdBy)!!,
            commentRepository.getCommentsByIssueID(issueEntry.id).toMutableList(),
            issueRepository.getIssueLinks(issueEntry.id).toMutableList()
        )
    }

    override fun getIssueLinks(id: Int): List<IssueLink> {
        return issueLinks.filter { it.linker == id }.map {
            val title = issues.first { it2 -> it2.id == it.linked }.title
            IssueLink(it.linked, title)
        }
    }

    override fun getAllIssues() : List<Issue> {
        return issues.map { issueRepository.getIssueByID(it.id)!! }
    }
}