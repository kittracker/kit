package edu.kitt.orm

import CollaboratorEntryRequest
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.orm.requests.ProjectEntryRequest
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction

class ExposedProjectRepository : ProjectRepository {

    override suspend fun getProjectByID(id: Int): Project? {
        return newSuspendedTransaction(Dispatchers.IO) {
            ProjectDAO.findById(id)?.let(::mapProjectDAOtoProject)
        }
    }

    override suspend fun getCollaboratorsByProjectID(projectId: Int): List<User> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val project = ProjectDAO.findById(projectId)
            project?.collaborators?.map(::mapUserDAOtoUser) ?: listOf()
        }
    }

    override suspend fun getAllProjects(): List<Project> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val projects = ProjectDAO.all()
            projects.map(::mapProjectDAOtoProject)
        }
    }

    override suspend fun getProjectsByUserID(userID: Int): List<Project> {
        return newSuspendedTransaction(Dispatchers.IO) {
            val ownedProjectIds = Projects
                .selectAll()
                .where { Projects.ownerID eq userID }
                .map { it[Projects.id] }

            val collaboratedProjectIds = Collaborators
                .selectAll()
                .where { Collaborators.userID eq userID }
                .map { it[Collaborators.projectID] }

            val allProjectIds = (ownedProjectIds + collaboratedProjectIds).toSet()

            if (allProjectIds.isEmpty()) {
                return@newSuspendedTransaction emptyList()
            }

            ProjectDAO.find { Projects.id inList allProjectIds }
                .map(::mapProjectDAOtoProject)
        }
    }

    override suspend fun createProject(project: ProjectEntryRequest): Project? {
        return newSuspendedTransaction (Dispatchers.IO){
        ProjectDAO.new {
            name = project.name!!
            description = project.description!!
            archived = project.archived!!
            owner = UserDAO.findById(project.ownerID!!)!!
            }.let(::mapProjectDAOtoProject)
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

            mapProjectDAOtoProject(projectDAO)
        }
    }

    override suspend fun deleteProject(id: Int): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            val rowsDeleted = Projects.deleteWhere {
                Projects.id eq id
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
            mapUserDAOtoUser(user)
        }
    }

    override suspend fun removeCollaboratorToProject(collaborator: CollaboratorEntryRequest): Boolean {
        return newSuspendedTransaction(Dispatchers.IO) {
            val rowsDeleted = Collaborators.deleteWhere {
                (Collaborators.projectID eq collaborator.projectID) and (Collaborators.userID eq collaborator.userID)
            }
            rowsDeleted == 1
        }
    }
}