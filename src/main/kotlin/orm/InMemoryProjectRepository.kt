package edu.kitt.orm

import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.issueRepository
import edu.kitt.orm.entries.CollaboratorEntry
import edu.kitt.orm.entries.ProjectEntry
import edu.kitt.projectRepository
import edu.kitt.userRepository

class InMemoryProjectRepository : ProjectRepository {
    private val projectCollaborators = listOf(
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

    private val projects = listOf(
        ProjectEntry(1, "project_name", "project_description", false, 1),
        ProjectEntry(2, "project_name", "project_description", false, 2),
        ProjectEntry(3, "project_name", "project_description", false, 3),
    )

    override fun getProjectByID(id: Int): Project? {
        val projectEntry = projects.firstOrNull { it.id == id }
        if (projectEntry == null) return null
        return Project(
            projectEntry.id,
            projectEntry.name,
            projectEntry.description,
            projectEntry.archived,
            // FIXME: this can throw
            userRepository.getUserByID(projectEntry.ownerID)!!,
            projectRepository.getCollaboratorsByProjectID(projectEntry.id).toMutableList(),
            issueRepository.getIssuesByProjectID(projectEntry.id).toMutableList()
        )
    }

    override fun getCollaboratorsByProjectID(projectId: Int): List<User> {
        return projectCollaborators.filter { it.projectID == projectId }.map {
            // FIXME: this can throw
            userRepository.getUserByID(it.userID)!!
        }
    }

    override fun getAllProjects(): List<Project> {
        return projects.map { projectRepository.getProjectByID(it.id)!! }
    }
}