package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.domainmodel.Issue
import edu.kitt.domainmodel.IssueLink
import edu.kitt.domainmodel.IssueStatus
import edu.kitt.domainmodel.Project
import edu.kitt.domainmodel.ProjectSummary
import edu.kitt.domainmodel.User
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Users : IntIdTable() {
    val userName = varchar("username", 50).uniqueIndex()
    val emailAddress = varchar("email_address", 255)
    val passwordHash = varchar("password_hash", 255)

    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val notificationsActive = bool("notifications_active")
}

object Projects : IntIdTable() {
    val name = varchar("name", 255)
    val description = text("description")
    val archived = bool("archived")
    val ownerID = reference("owner_id", Users, onDelete = ReferenceOption.CASCADE)
}

object Issues : IntIdTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val status = enumeration<IssueStatus>("status") // Ensure IssueStatus enum is defined
    val createdBy = reference("created_by", Users, onDelete = ReferenceOption.RESTRICT) // Or CASCADE
    val projectID = reference("project_id", Projects, onDelete = ReferenceOption.CASCADE)
    // val createdAt = datetime("created_at").clientDefault { org.joda.time.DateTime.now() } // Example for datetime
}

object Comments : IntIdTable() {
    val author = reference("author", Users, onDelete = ReferenceOption.RESTRICT)
    val text = text("text")
    val issue = reference("issue_id", Issues, onDelete = ReferenceOption.CASCADE)
}

object IssueLinks : Table() {
    val linker = reference("linker", Issues, onDelete = ReferenceOption.CASCADE)
    val linked = reference("linked", Issues, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(linker, linked, name = "PK_IssueLinks_ID")
}

object Collaborators : Table() {
    val userID = reference("user_id", Users, onDelete = ReferenceOption.CASCADE) // Corrected from Issues to Users
    val projectID = reference("project_id", Projects, onDelete = ReferenceOption.CASCADE) // Corrected from Issues to Projects

    override val primaryKey = PrimaryKey(userID, projectID, name = "PK_Collaborators_ID")
}


// --- Exposed DAOs ---

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(Users)

    var userName by Users.userName
    var emailAddress by Users.emailAddress
    var passwordHash by Users.passwordHash

    var firstName by Users.firstName
    var lastName by Users.lastName
    var notificationsActive by Users.notificationsActive
}

class ProjectDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ProjectDAO>(Projects)

    var name by Projects.name
    var description by Projects.description
    var archived by Projects.archived
    var owner by UserDAO referencedOn Projects.ownerID
    val collaborators by UserDAO via Collaborators // This will load Users
    val issues by IssueDAO referrersOn Issues.projectID
}

class IssueDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<IssueDAO>(Issues)

    var title by Issues.title
    var description by Issues.description
    var status by Issues.status
    var createdBy by UserDAO referencedOn Issues.createdBy
    var project by ProjectDAO referencedOn Issues.projectID
    val links by IssueDAO.via(IssueLinks.linker, IssueLinks.linked) // This will load Issues
    val comments by CommentDAO referrersOn Comments.issue
}

class CommentDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentDAO>(Comments)

    var author by UserDAO referencedOn Comments.author
    var text by Comments.text
    var issue by IssueDAO referencedOn Comments.issue
}


// --- Mappers ---

fun mapUserDAOtoUser(userDAO: UserDAO) = User(
    id = userDAO.id.value,
    username = userDAO.userName,
    emailAddress = userDAO.emailAddress,
    firstName = userDAO.firstName ?: "",
    lastName = userDAO.lastName ?: "",
    notificationsActive = userDAO.notificationsActive,
)

// Helper to map Project's owner and collaborators, avoiding recursion if issues or collaborators are not needed immediately
// This mapper will eagerly load collaborators. For 'issues', it calls mapIssueDAOtoIssue which is defined later.
fun mapProjectDAOtoProject(projectDAO: ProjectDAO) = Project(
    id = projectDAO.id.value,
    name = projectDAO.name,
    description = projectDAO.description,
    archived = projectDAO.archived,
    owner = mapUserDAOtoUser(projectDAO.owner),
    collaborators = projectDAO.collaborators.map(::mapUserDAOtoUser).toMutableList(),
    issues = projectDAO.issues.map(::mapIssueDAOtoIssue).toMutableList()
)


fun mapProjectDAOtoProjectSummary(projectDAO: ProjectDAO) = ProjectSummary(
    id = projectDAO.id.value,
    name = projectDAO.name,
    owner = mapUserDAOtoUser(projectDAO.owner),
)


fun mapCommentDAOtoComment(commentDAO: CommentDAO) = Comment(
    id = commentDAO.id.value,
    author = mapUserDAOtoUser(commentDAO.author),
    text = commentDAO.text
)


fun mapIssueDAOtoIssue(issueDAO: IssueDAO): Issue {
    return Issue(
        id = issueDAO.id.value,
        title = issueDAO.title,
        description = issueDAO.description,
        status = issueDAO.status,
        project = mapProjectDAOtoProjectSummary(issueDAO.project),
        createdBy = mapUserDAOtoUser(issueDAO.createdBy),
        comments = issueDAO.comments.map(::mapCommentDAOtoComment).toMutableList(),
        links = issueDAO.links.map { linkedIssueDAO ->
            IssueLink(
                id = issueDAO.id.value, // The current issue is the linker
                title = linkedIssueDAO.title // The issue found via the link is the linked one
            )
        }.toMutableList()
    )
}
