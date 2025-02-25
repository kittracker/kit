package edu.kitt.orm

import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User

interface ProjectRepository {
    fun getProjectByID(id: Int): Project?
    fun getCollaboratorsByProjectID(projectId: Int): List<User>
    fun getAllProjects(): List<Project>
}