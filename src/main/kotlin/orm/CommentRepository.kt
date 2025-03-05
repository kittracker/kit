package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.entries.CommentEntry
import edu.kitt.orm.requests.CommentEntryRequest

interface CommentRepository {
    fun removeCommentByID(id: Int): Boolean
    fun getCommentsByIssueID(id: Int): List<Comment>
    fun getCommentsByUserID(id: Int): List<Comment>
    fun createComment(comment: CommentEntryRequest): Comment?
    fun editComment(comment: CommentEntryRequest): Comment?
}