package edu.kitt.orm

import edu.kitt.domainmodel.Project

interface ProjectRepository {
    fun getProjectByID(id: Int): Project?
    fun getAllProjects(): List<Project>
}