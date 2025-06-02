package edu.kitt.orm

import edu.kitt.domainmodel.IssueStatus
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UIntIdTable
import org.jetbrains.exposed.v1.dao.UIntEntity
import org.jetbrains.exposed.v1.dao.UIntEntityClass

object Users : UIntIdTable() {
    val userName = varchar("username", 50).index()
    val emailAddress = varchar("email_address", 255)
}

class UserDAO(id: EntityID<UInt>) : UIntEntity(id) {
    companion object : UIntEntityClass<UserDAO>(Users)

    val userName by Users.userName
    val emailAddress by Users.emailAddress
}

object Projects : UIntIdTable() {
    val name = varchar("name", 255)
    val description = text("description")
    val archived = bool("archived")
    val ownerID = reference("owner_id", Users)
}

class ProjectDAO(id: EntityID<UInt>) : UIntEntity(id) {
    companion object : UIntEntityClass<ProjectDAO>(Projects)

    val name by Projects.name
    val description by Projects.description
    val archived by Projects.archived
    val owner by UserDAO referencedOn Projects.ownerID
    val collaborators by UserDAO via Collaborators
}

object Issues : UIntIdTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val status = enumeration<IssueStatus>("status") // Maybe define enum here?
    val createdBy = reference("created_by", Users)
    val projectID = reference("project_id", Projects)
}

class IssueDAO(id: EntityID<UInt>) : UIntEntity(id) {
    companion object : UIntEntityClass<IssueDAO>(Issues)

    val title by Issues.title
    val description by Issues.description
    val status by Issues.status
    val createdBy by UserDAO referencedOn Issues.createdBy
    val project by ProjectDAO referencedOn Issues.projectID
    val links by IssueDAO via IssueLinks
}

object Comments : UIntIdTable() {
    val author = reference("author", Users)
    val text = text("text")
    val issue = reference("issue_id", Issues)
}

class CommentDAO(id: EntityID<UInt>) : UIntEntity(id) {
    companion object : UIntEntityClass<CommentDAO>(Comments)

    var author by UserDAO referencedOn Comments.author
    var text by Comments.text
    var issue by IssueDAO referencedOn Comments.issue
}

object IssueLinks : Table() {
    val linker = reference("linker", Issues)
    val linked = reference("linked", Issues)

    override val primaryKey = PrimaryKey(linker, linked, name = "PK_IssueLinks_ID")
}

object Collaborators : Table() {
    val userID = reference("user_id", Issues)
    val projectID = reference("project_id", Issues)

    override val primaryKey = PrimaryKey(userID, projectID, name = "PK_Collaborators_ID")
}