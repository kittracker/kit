package edu.kitt.orm

import edu.kitt.domainmodel.IssueStatus
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Users : IntIdTable() {
    val userName = varchar("username", 50).index()
    val emailAddress = varchar("email_address", 255)
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)

    val userName by Users.userName
    val emailAddress by Users.emailAddress
}

object Projects : IntIdTable() {
    val name = varchar("name", 255)
    val description = text("description")
    val archived = bool("archived")
    val ownerID = reference("owner_id", Users)
}

class ProjectDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProjectDAO>(Projects)

    var name by Projects.name
    var description by Projects.description
    var archived by Projects.archived
    var owner by UserDAO referencedOn Projects.ownerID
    val collaborators by UserDAO via Collaborators
    val issues by IssueDAO referrersOn Issues.projectID
}

object Issues : IntIdTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val status = enumeration<IssueStatus>("status") // Maybe define enum here?
    val createdBy = reference("created_by", Users)
    val projectID = reference("project_id", Projects)
}

class IssueDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IssueDAO>(Issues)

    val title by Issues.title
    val description by Issues.description
    val status by Issues.status
    val createdBy by UserDAO referencedOn Issues.createdBy
    val project by ProjectDAO referencedOn Issues.projectID
    val links by IssueDAO via IssueLinks
    val comments by CommentDAO referrersOn Comments.issue
}

object Comments : IntIdTable() {
    val author = reference("author", Users)
    val text = text("text")
    val issue = reference("issue_id", Issues)
}

class CommentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentDAO>(Comments)

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