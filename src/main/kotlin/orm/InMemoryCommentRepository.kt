package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.entries.CommentEntry
import edu.kitt.userRepository

class InMemoryCommentRepository : CommentRepository {
    private val comments = listOf(
        CommentEntry(1, 1, "this is a comment", 1),
        CommentEntry(2, 2, "this is a comment", 1),
        CommentEntry(3, 3, "this is a comment", 1),
    )

    override fun getCommentsByIssueID(id: Int): List<Comment> {
        return comments.filter { it.issueID == id }.map {
            val user = userRepository.getUserByID(it.id);
            // FIXME: this will throw if user is invalid
            Comment(it.id, user!!, it.text)
        }
    }

    override fun getCommentsByUserID(id: Int): List<Comment> {
        return comments.filter { it.author == id }.map {
            val user = userRepository.getUserByID(it.id);
            // FIXME: this will throw if user is invalid
            Comment(it.id, user!!, it.text)
        }
    }
}