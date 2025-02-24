package edu.kitt.orm

import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.User

class InMemoryProjectRepository : ProjectRepository {
    private val users = listOf<User>(
        User(1, "matteo@gmail.com", "cardisk"),
        User(2, "leonardo@gmail.com", "spectrev333"),
        User(3, "mirco@gmail.com", "mircocaneschi"),
    )

    private val projects = listOf<Project>(
        Project(1, "project_name", "project_description",
            false, users[0], mutableListOf(users[1], users[2]), mutableListOf()),
        Project(2, "project_name", "project_description",
            false, users[1], mutableListOf(users[0], users[2]), mutableListOf()),
        Project(3, "project_name", "project_description",
            false, users[2], mutableListOf(users[0], users[1]), mutableListOf()),
    )

    override fun getProjectByID(id: Int): Project? {
        return projects.firstOrNull { it.id == id }
    }

    override fun getAllProjects(): List<Project> {
        return projects
    }
}