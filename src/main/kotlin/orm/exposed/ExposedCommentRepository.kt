package edu.kitt.orm.exposed

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.CommentRepository
import edu.kitt.orm.requests.CommentEntryRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedCommentRepository : CommentRepository {
    override suspend fun removeCommentByID(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.Companion.findById(id)?.delete() != null
            }
        }
    }

    override suspend fun getCommentByID(id: Int): Comment? {
        return withContext(Dispatchers.IO) {
            transaction {
                val commentDAO = CommentDAO.Companion.findById(id)
                if (commentDAO == null) return@transaction null

                mapCommentDAOtoComment(commentDAO)
            }
        }
    }

    override suspend fun getCommentsByIssueID(id: Int): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.Companion.find { Comments.issue eq id }.map {
                    mapCommentDAOtoComment(it)
                }
            }
        }
    }

    override suspend fun getCommentsByUserID(id: Int): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                // Assuming Comments.author is a reference to UserDAO's ID (which is UInt)
                CommentDAO.Companion.find { Comments.author eq id }.map {
                    mapCommentDAOtoComment(it)
                }
            }
        }
    }

    override suspend fun createComment(comment: CommentEntryRequest): Comment? {
        return withContext(Dispatchers.IO) {
            transaction {
                val user = UserDAO.Companion.findById(comment.author ?: throw IllegalArgumentException("Author ID must be set"))
                val issue = IssueDAO.Companion.findById(comment.issueID ?: throw IllegalArgumentException("Issue ID must be set"))

                if (user == null || issue == null) {
                    return@transaction null
                }

                val newCommentDAO = CommentDAO.Companion.new {
                    author = user
                    text = comment.text?: throw IllegalArgumentException("Text must be set")
                    this.issue = issue
                }
                mapCommentDAOtoComment(newCommentDAO)
            }
        }
    }

    override suspend fun editComment(comment: CommentEntryRequest): Comment? {
        val commentIdToEdit = comment.id ?: return null // ID is mandatory for editing

        return withContext(Dispatchers.IO) {
            transaction {
                val commentDAO = CommentDAO.Companion.findById(commentIdToEdit)
                val newAuthorDAO = UserDAO.Companion.findById(comment.author ?: throw IllegalArgumentException("Author ID must be set"))
                val issueDAO = IssueDAO.Companion.findById(comment.issueID  ?: throw IllegalArgumentException("Issue ID must be set"))

                // If either the comment or the author do not exist, return null
                if (commentDAO == null || newAuthorDAO == null || issueDAO == null) {
                    // Comment to edit not found
                    return@transaction null
                }

                commentDAO.text = comment.text ?: commentDAO.text
                commentDAO.author = newAuthorDAO
                commentDAO.issue = issueDAO

                mapCommentDAOtoComment(commentDAO)
            }
        }
    }

}