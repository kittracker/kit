package edu.kitt.orm

import edu.kitt.commentRepository
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.issueRepository
import edu.kitt.orm.entries.IssueEntry
import edu.kitt.orm.entries.IssueLinkEntry
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import edu.kitt.userRepository

class InMemoryIssueRepository : IssueRepository {
    private val issueLinks = mutableListOf(
        IssueLinkEntry(1, 2),
        IssueLinkEntry(2, 1),
        IssueLinkEntry(3, 1),
    )

    private val issues = mutableListOf(
        IssueEntry(1, "title", "description", IssueStatus.OPEN, 1, 1),
        IssueEntry(2, "title", "description", IssueStatus.OPEN, 2, 1),
        IssueEntry(3, "title", "description", IssueStatus.OPEN, 3, 1),
    )

    override fun createIssue(issue: IssueEntryRequest): Issue? {
        val new = IssueEntry(
            id = issues.last().id + 1,
            title = issue.title ?: return null,
            description = issue.description ?: return null,
            status = IssueStatus.OPEN,
            createdBy = issue.createdBy ?: return null,
            projectID = issue.projectID ?: return null
        )
        issues.add(new)
        return getIssueByID(new.id)
    }

    override fun getIssuesByProjectID(id: Int): List<Issue> {
        return issues.filter { it.projectID == id }.map {
            // FIXME: this can throw
            issueRepository.getIssueByID(it.id!!)!!
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
            commentRepository.getCommentsByIssueID(issueEntry.id!!).toMutableList(),
            issueRepository.getIssueLinks(issueEntry.id!!).toMutableList()
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

    override fun editIssue(issue: IssueEntryRequest): Issue? {
        val stored = issues.find { it.id == issue.id } ?: return null
        val edited = stored.copy(
            title = issue.title ?: stored.title,
            description = issue.description ?: stored.description,
            status = issue.status ?: stored.status,
        )

        val index = issues.indexOf(stored)
        issues[index] = edited

        return getIssueByID(edited.id)
    }

    override fun deleteIssue(id: Int): Boolean {
        issueLinks.removeIf { it.linker == id }
        commentRepository.getCommentsByIssueID(id).forEach { // TODO: add method to CommentRepository
            commentRepository.removeCommentByID(it.id)
        }
        return issues.removeIf { it.id == id }
    }

    override fun linkIssues(link: IssueLinkEntryRequest): IssueLink? {
        if (issues.none { it.id == link.linked }) return null
        val existentLink = issueLinks.find { it.linker == link.linker && it.linked == link.linked }
        if (existentLink != null) {
            // Asserting the existence of the issue because of the previous checks
            val issue = getIssueByID(link.linked)!!
            return IssueLink(
                id = issue.id,
                title = issue.title,
            )
        }

        val new = IssueLinkEntry(
            linker = link.linker,
            linked = link.linked,
        )
        issueLinks.add(new)
        // Asserting the existence of the issue because of the previous checks
        return IssueLink(new.linked, getIssueByID(new.linked)!!.title)
    }

    override fun deleteLink(link: IssueLinkEntryRequest): Boolean {
        return issueLinks.removeIf { it.linker == link.linker && it.linked == link.linked }
    }
}