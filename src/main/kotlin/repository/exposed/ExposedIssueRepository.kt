package edu.kitt.orm.exposed

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.orm.IssueRepository
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedIssueRepository() : IssueRepository {
    override suspend fun createIssue(issue: IssueEntryRequest): Issue? {

        return newSuspendedTransaction(Dispatchers.IO) {
            val parentProject =
                ProjectDAO.Companion.findById(issue.projectID ?: throw IllegalArgumentException("Project ID must be set"))
            val owner = UserDAO.Companion.findById(issue.owner ?: throw IllegalArgumentException("Creator ID must be set"))

            if (owner == null || parentProject == null) return@newSuspendedTransaction null

            val newIssue = IssueDAO.Companion.new {
                title = issue.title ?: throw IllegalArgumentException("Title must be set")
                description = issue.description ?: throw IllegalArgumentException("Description must be set")
                status = IssueStatus.OPEN
                this.owner = owner
                project = parentProject
            }
            mapIssueDAOtoIssue(newIssue)
        }

    }

    override suspend fun getIssuesByProjectID(id: Int): List<Issue> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val project = ProjectDAO.Companion.findById(id)
            project?.issues?.map(::mapIssueDAOtoIssue) ?: listOf()
        }
    }

    override suspend fun getIssueByID(id: Int): Issue? {
        return newSuspendedTransaction(Dispatchers.IO) {
            IssueDAO.Companion.findById(id)?.let(::mapIssueDAOtoIssue)
        }
    }

    override suspend fun getIssueLinks(id: Int): List<IssueLink> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val issue =
                IssueDAO.Companion.findById(id)?.let(::mapIssueDAOtoIssue) ?: return@newSuspendedTransaction listOf<IssueLink>()
            issue.links
        }
    }

    override suspend fun getAllIssues(): List<Issue> {
        return newSuspendedTransaction(Dispatchers.IO) {
            IssueDAO.Companion.all().map(::mapIssueDAOtoIssue)
        }
    }

    override suspend fun editIssue(issue: IssueEntryRequest): Issue? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val modifiedIssueDAO = IssueDAO.Companion.findById(
                issue.id ?: throw IllegalArgumentException("Issue ID must be set")
            ) ?: throw IllegalArgumentException("Issue does not exist")

            modifiedIssueDAO.apply {
                title = issue.title ?: modifiedIssueDAO.title
                description = issue.description ?: modifiedIssueDAO.description
                status = issue.status ?: modifiedIssueDAO.status
            }
            mapIssueDAOtoIssue(modifiedIssueDAO)
        }
    }

    override suspend fun deleteIssue(id: Int): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            IssueDAO.Companion.findById(id)?.delete() != null
        }
    }

    override suspend fun linkIssues(link: IssueLinkEntryRequest): IssueLink? {
        return newSuspendedTransaction(Dispatchers.IO) {
            try {
                val linkedIssue =
                    IssueDAO.Companion.findById(link.linked) ?: throw IllegalArgumentException("Linked issue does not exist")
                IssueLinks.insert {
                    it[linker] = link.linker
                    it[linked] = link.linked
                }
                return@newSuspendedTransaction IssueLink(link.linker, linkedIssue.title)
            } catch (e: Exception) {
            }
            null
        }
    }

    override suspend fun deleteLink(link: IssueLinkEntryRequest): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            val deletedRowsCount = IssueLinks.deleteWhere {
                IssueLinks.linker eq link.linker
                IssueLinks.linked eq link.linked
            }
            deletedRowsCount == 1
        }
    }
}