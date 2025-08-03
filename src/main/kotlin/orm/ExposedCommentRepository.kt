package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.requests.CommentEntryRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedCommentRepository : CommentRepository {
    override suspend fun removeCommentByID(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.findById(id)?.delete() != null
            }
        }
    }

    override suspend fun getCommentByID(id: Int): Comment? {
        return withContext(Dispatchers.IO) {
            transaction {
                val commentDAO = CommentDAO.findById(id)
                if (commentDAO == null) return@transaction null

                mapCommentDAOtoComment(commentDAO)
            }
        }
    }

    override suspend fun getCommentsByIssueID(id: Int): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                CommentDAO.find { Comments.issue eq id }.map {
                    mapCommentDAOtoComment(it)
                }
            }
        }
    }

    override suspend fun getCommentsByUserID(id: Int): List<Comment> {
        return withContext(Dispatchers.IO) {
            transaction {
                // Assuming Comments.author is a reference to UserDAO's ID (which is UInt)
                CommentDAO.find { Comments.author eq id }.map {
                    mapCommentDAOtoComment(it)
                }
            }
        }
    }

    override suspend fun createComment(comment: CommentEntryRequest): Comment? {
        return withContext(Dispatchers.IO) {
            transaction {
                val user = UserDAO.findById(comment.author ?: return@transaction null)
                val issue = IssueDAO.findById(comment.issueID ?: return@transaction null)

                if (user == null || issue == null) {
                    return@transaction null
                }

                val newCommentDAO = CommentDAO.new {
                    author = user
                    text = comment.text!! // TODO: add proper check before
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
                val commentDAO = CommentDAO.findById(commentIdToEdit)
                val newAuthorDAO = UserDAO.findById(comment.author ?: return@transaction null)
                val issueDAO = IssueDAO.findById(comment.issueID ?: throw IllegalArgumentException("Issue ID is missing"))

                // If either the comment or the author do not exist, return null
                if (commentDAO == null || newAuthorDAO == null || issueDAO == null) {
                    // Comment to edit not found
                    //return@transaction null
                    throw IllegalArgumentException("Comment to edit not found")
                }

                commentDAO.text = comment.text!!
                commentDAO.author = newAuthorDAO
                commentDAO.issue = issueDAO

                mapCommentDAOtoComment(commentDAO)
            }
        }
    }

}