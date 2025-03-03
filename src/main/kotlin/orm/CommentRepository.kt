package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.entries.CommentEntry

interface CommentRepository {
    fun getCommentsByIssueID(id: Int): List<Comment>
    fun getCommentsByUserID(id: Int): List<Comment>
    fun createComment(comment: CommentEntry): Comment?
}