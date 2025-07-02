package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.domainmodel.User
import edu.kitt.orm.requests.IssueEntryRequest
import edu.kitt.orm.requests.IssueLinkEntryRequest
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedIssueRepository() : IssueRepository {
    override suspend fun createIssue(issue: IssueEntryRequest): Issue? {

        return newSuspendedTransaction (Dispatchers.IO){
            val parentProject = ProjectDAO.findById(issue.projectID!!)
            val owner = UserDAO.findById(issue.createdBy!!)

            if (owner == null || parentProject == null) return@newSuspendedTransaction null

            val newIssue = IssueDAO.new {
                title = issue.title!!
                description = issue.description!!
                status = IssueStatus.OPEN
                createdBy = owner
                project = parentProject
            }
            mapIssueDAOtoIssue(newIssue)
        }

    }

    override suspend fun getIssuesByProjectID(id: Int): List<Issue> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val project = ProjectDAO.findById(id)
            project?.issues?.map(::mapIssueDAOtoIssue) ?: listOf()
        }
    }

    override suspend fun getIssueByID(id: Int): Issue? {
        return newSuspendedTransaction(Dispatchers.IO) {
            IssueDAO.findById(id)?.let(::mapIssueDAOtoIssue)
        }
    }

    override suspend fun getIssueLinks(id: Int): List<IssueLink> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val issue = IssueDAO.findById(id)?.let(::mapIssueDAOtoIssue) ?: return@newSuspendedTransaction listOf<IssueLink>()
            issue.links
        }
    }

    override suspend fun getAllIssues(): List<Issue> {
        return newSuspendedTransaction(Dispatchers.IO) {
            IssueDAO.all().map(::mapIssueDAOtoIssue)
        }
    }

    override suspend fun editIssue(issue: IssueEntryRequest): Issue? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val modifiedIssueDAO = IssueDAO.findById(issue.id!!) ?: return@newSuspendedTransaction null

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
            IssueDAO.findById(id)?.delete() != null
        }
    }

    override suspend fun linkIssues(link: IssueLinkEntryRequest): IssueLink? {
        return newSuspendedTransaction(Dispatchers.IO) {
            try {
                val linkedIssue = IssueDAO.findById(link.linked)!!
                IssueLinks.insert {
                    it[linker] = link.linker
                    it[linked] = link.linked
                }
                return@newSuspendedTransaction IssueLink(link.linker, linkedIssue.title)
            } catch (e: Exception) {}
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