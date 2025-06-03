package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.domainmodel.User
import edu.kitt.orm.requests.CommentEntryRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedCommentRepository : CommentRepository {
    override suspend fun removeCommentByID(id: UInt): Boolean {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.findById(id.toUInt())?.delete() != null
            }
        }
    }

    private fun mapCommentDAOToDomain(commentDAO: CommentDAO): Comment {
        return Comment(
            id = commentDAO.id.value,
            author = User(
                id = commentDAO.author.id.value,
                emailAddress = commentDAO.author.emailAddress,
                username = commentDAO.author.userName
            ),
            text = commentDAO.text
        )
    }

    override suspend fun getCommentsByIssueID(id: UInt): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.find { Comments.issue eq id.toUInt() }.map {
                    mapCommentDAOToDomain(it)
                }
            }
        }
    }

    override suspend fun getCommentsByUserID(id: UInt): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                // Assuming Comments.author is a reference to UserDAO's ID (which is UInt)
                CommentDAO.find { Comments.author eq id.toUInt() }.map {
                    mapCommentDAOToDomain(it)
                }
            }
        }
    }

    override suspend fun createComment(entry: CommentEntryRequest): Comment? {
        return withContext(Dispatchers.IO) {
            transaction {
                val user = UserDAO.findById(entry.author ?: return@transaction null)
                val issue = IssueDAO.findById(entry.issueID ?: return@transaction null)

                if (user == null || issue == null) {
                    return@transaction null
                }

                val newCommentDAO = CommentDAO.new {
                    author = user
                    text = entry.text!! // TODO: add proper check before
                    this.issue = issue
                }
                mapCommentDAOToDomain(newCommentDAO)
            }
        }
    }

    override suspend fun editComment(entry: CommentEntryRequest): Comment? {
        val commentIdToEdit = entry.id ?: return null // ID is mandatory for editing

        return withContext(Dispatchers.IO) {
            transaction {
                val commentDAO = CommentDAO.findById(commentIdToEdit)
                val newAuthorDAO = UserDAO.findById(entry.author ?: return@transaction null)
                val issueDAO = IssueDAO.findById(entry.issueID ?: return@transaction null)

                // If either the comment or the author do not exist, return null
                if (commentDAO == null || newAuthorDAO == null || issueDAO == null) {
                    // Comment to edit not found
                    return@transaction null
                }

                commentDAO.text = entry.text!!
                commentDAO.author = newAuthorDAO
                commentDAO.issue = issueDAO

                mapCommentDAOToDomain(commentDAO)
            }
        }
    }

}