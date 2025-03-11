package edu.kitt.orm

import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.orm.requests.ProjectEntryRequest

interface ProjectRepository {
    fun getProjectByID(id: Int): Project?
    fun getCollaboratorsByProjectID(projectId: Int): List<User>
    fun getAllProjects(): List<Project>
    fun createProject(project: ProjectEntryRequest): Project?
    fun editProject(project: ProjectEntryRequest): Project?
    fun deleteProject(id: Int): Boolean
}