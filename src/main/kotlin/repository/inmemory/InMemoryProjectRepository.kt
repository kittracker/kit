package edu.kitt.repository.inmemory

import CollaboratorEntryRequest
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.repository.IssueRepository
import edu.kitt.repository.ProjectRepository
import edu.kitt.repository.UserRepository
import edu.kitt.repository.entries.CollaboratorEntry
import edu.kitt.repository.entries.ProjectEntry
import edu.kitt.repository.requests.ProjectEntryRequest

class InMemoryProjectRepository(val userRepository: UserRepository, val issueRepository: IssueRepository) : ProjectRepository {
    private val projectCollaborators = mutableListOf(
        CollaboratorEntry(1, 1),
        CollaboratorEntry(1, 2),
        CollaboratorEntry(1, 3),
        CollaboratorEntry(2, 1),
        CollaboratorEntry(2, 2),
        CollaboratorEntry(2, 3),
        CollaboratorEntry(3, 1),
        CollaboratorEntry(3, 2),
        CollaboratorEntry(3, 3),
    )

    private val projects = mutableListOf(
        ProjectEntry(1, "project_name", "project_description", false, 1),
        ProjectEntry(2, "project_name", "project_description", false, 2),
        ProjectEntry(3, "project_name", "project_description", false, 3),
    )

    override suspend fun getProjectByID(id: Int): Project? {
        val projectEntry = projects.firstOrNull { it.id == id }
        if (projectEntry == null) return null
        return Project(
            projectEntry.id!!,
            projectEntry.name,
            projectEntry.description,
            projectEntry.archived,
            // FIXME: this can throw
            userRepository.getUserByID(projectEntry.ownerID)!!,
            this.getCollaboratorsByProjectID(projectEntry.id).toMutableList(),
            issueRepository.getIssuesByProjectID(projectEntry.id).toMutableList()
        )
    }

    override suspend fun getCollaboratorsByProjectID(projectId: Int): List<User> {
        return projectCollaborators.filter { it.projectID == projectId }.map {
            // FIXME: this can throw
            userRepository.getUserByID(it.userID)!!
        }
    }

    override suspend fun getAllProjects(): List<Project> {
        return projects.map { this.getProjectByID(it.id)!! }
    }

    override suspend fun getProjectsByUserID(userID: Int): List<Project> {
        TODO("Not yet implemented")
    }

    override suspend fun createProject(project: ProjectEntryRequest): Project? {
        val new = ProjectEntry(
            id = projects.last().id + 1,
            name = project.name ?: return null,
            description = project.description ?: "",
            archived = false,
            ownerID = 1,    // TODO: ownerID must be taken by the jwt
        )
        projects.add(new);
        return getProjectByID(new.id)
    }

    override suspend fun editProject(project: ProjectEntryRequest): Project? {
        val stored = projects.find { it.id == project.id } ?: return null
        val edited = stored.copy(
            name = project.name ?: stored.name,
            description = project.description ?: stored.description,
            archived = project.archived ?: stored.archived,
        )

        val index = projects.indexOf(stored)
        projects[index] = edited

        return getProjectByID(edited.id)
    }

    override suspend fun deleteProject(id: Int): Boolean {
        issueRepository.getIssuesByProjectID(id).forEach {
            issueRepository.deleteIssue(it.id)      // TODO: add method to issueRepository
        }
        projectCollaborators.removeIf { it.projectID == id }
        return projects.removeIf { it.id == id }
    }

    override suspend fun addCollaboratorToProject(collaborator: CollaboratorEntryRequest): User? {
        if (projects.none { it.id == collaborator.projectID }) return null

        val existentCollaborator = projectCollaborators.find {
            it.projectID == collaborator.projectID && it.userID == collaborator.userID
        }
        if (existentCollaborator != null) return userRepository.getUserByID(collaborator.userID)

        if (userRepository.getUserByID(collaborator.userID) == null) return null

        val new = CollaboratorEntry(
            userID = collaborator.userID,
            projectID = collaborator.projectID,
        )
        projectCollaborators.add(new)
        // Asserting here is useless because the return type is the same, but technically
        // it should never return null at this point
        return userRepository.getUserByID(collaborator.userID)!!
    }

    override suspend fun removeCollaboratorToProject(collaborator: CollaboratorEntryRequest): Boolean {
        return projectCollaborators.removeIf { it.userID == collaborator.userID && it.projectID == collaborator.projectID }
    }
}