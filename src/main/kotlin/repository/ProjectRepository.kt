package edu.kitt.repository

import CollaboratorEntryRequest
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User
import edu.kitt.repository.requests.ProjectEntryRequest

interface ProjectRepository {
    suspend fun getProjectByID(id: Int): Project?
    suspend fun getCollaboratorsByProjectID(projectId: Int): List<User>
    suspend fun getAllProjects(): List<Project>
    suspend fun getProjectsByUserID(userID: Int): List<Project>
    suspend fun createProject(project: ProjectEntryRequest): Project?
    suspend fun editProject(project: ProjectEntryRequest): Project?
    suspend fun deleteProject(id: Int): Boolean
    suspend fun addCollaboratorToProject(collaborator: CollaboratorEntryRequest): User?
    suspend fun removeCollaboratorToProject(collaborator: CollaboratorEntryRequest): Boolean
}