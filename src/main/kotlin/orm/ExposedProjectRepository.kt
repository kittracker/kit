package edu.kitt.orm

import CollaboratorEntryRequest
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.domainmodel.Comment
import edu.kitt.domainmodel.IssueLink
import edu.kitt.orm.requests.ProjectEntryRequest
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedProjectRepository : ProjectRepository {

    private fun mapProjectDAOToDomain(project: ProjectDAO): Project = Project(
        id = project.id.value,
        name = project.name,
        description = project.description,
        archived = project.archived,
        owner = User(
            id = project.owner.id.value,
            emailAddress = project.owner.emailAddress,
            username = project.owner.userName
        ),
        collaborators = project.collaborators.map { collaborator ->
            User(
                id = collaborator.id.value,
                emailAddress = collaborator.emailAddress,
                username = collaborator.userName
            )
        } as MutableList<User>,
        issues = project.issues.map { issue ->
            Issue(
                id = issue.id.value,
                title = issue.title,
                description = issue.description,
                status = issue.status,
                createdBy = User(
                    id = issue.createdBy.id.value,
                    emailAddress = issue.createdBy.emailAddress,
                    username = issue.createdBy.userName
                ),
                comments = issue.comments.map { comment ->
                    Comment(
                        id = comment.id.value,
                        author = User(
                            id = comment.author.id.value,
                            emailAddress = comment.author.emailAddress,
                            username = comment.author.userName
                        ),
                        text = comment.text
                    )
                } as MutableList<Comment>,
                links = issue.links.map { link ->
                    IssueLink(
                        id = link.id.value,
                        title = link.title
                    )
                } as MutableList<IssueLink>,
            )
        } as MutableList<Issue>
    )

    override suspend fun getProjectByID(id: Int): Project? {
        return newSuspendedTransaction(Dispatchers.IO) {
            ProjectDAO.findById(id.toUInt())?.let(::mapProjectDAOToDomain)
        }
    }

    override suspend fun getCollaboratorsByProjectID(projectId: Int): List<User> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val project = ProjectDAO.findById(projectId.toUInt())
            project?.collaborators?.map { collaborator ->
                User(
                    id = collaborator.id.value,
                    emailAddress = collaborator.emailAddress,
                    username = collaborator.userName
                )
            } ?: listOf()
        }
    }

    override suspend fun getAllProjects(): List<Project> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val projects = ProjectDAO.all()
            projects.map(::mapProjectDAOToDomain)
        }
    }

    override suspend fun createProject(project: ProjectEntryRequest): Project? {
        return newSuspendedTransaction (Dispatchers.IO){
        ProjectDAO.new {
            name = project.name!!
            description = project.description!!
            archived = project.archived!!
            owner = UserDAO.findById(project.ownerID!!)!!
            }.let(::mapProjectDAOToDomain)
        }
    }

    override suspend fun editProject(project: ProjectEntryRequest): Project? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val projectDAO = ProjectDAO.findById(project.id!!) ?: return@newSuspendedTransaction null
            projectDAO.apply {
                name = project.name!!
                description = project.description!!
                archived = project.archived!!
                owner = UserDAO.findById(project.ownerID!!)!!
            }

            mapProjectDAOToDomain(projectDAO)
        }
    }

    override suspend fun deleteProject(id: Int): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            val rowsDeleted = Projects.deleteWhere {
                Projects.id eq id.toUInt()
            }
            rowsDeleted == 1
        }
    }

    // FIXME: Maybe returning boolean makes more sense
    override suspend fun addCollaboratorToProject(collaborator: CollaboratorEntryRequest): User? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val user = UserDAO.findById(collaborator.userID) ?: return@newSuspendedTransaction null
            Collaborators.insert {
                it[projectID] = collaborator.projectID
                it[userID] = user.id.value
            }
            User(
                id = user.id.value,
                emailAddress = user.emailAddress,
                username = user.userName
            )
        }
    }

    override suspend fun removeCollaboratorToProject(collaborator: CollaboratorEntryRequest): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            val rowsDeleted = Collaborators.deleteWhere {
                Collaborators.projectID eq collaborator.projectID
                Collaborators.userID eq collaborator.userID
            }
            rowsDeleted == 1
        }
    }
}