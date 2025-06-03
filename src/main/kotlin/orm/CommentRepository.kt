package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.requests.CommentEntryRequest

interface CommentRepository {
    suspend fun removeCommentByID(id: UInt): Boolean
    suspend fun getCommentsByIssueID(id: UInt): List<Comment>
    suspend fun getCommentsByUserID(id: UInt): List<Comment>
    suspend fun createComment(comment: CommentEntryRequest): Comment?
    suspend fun editComment(comment: CommentEntryRequest): Comment?
}