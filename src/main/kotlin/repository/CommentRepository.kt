package edu.kitt.repository

import edu.kitt.domainmodel.Comment
import edu.kitt.repository.requests.CommentEntryRequest

interface CommentRepository {
    suspend fun removeCommentByID(id: Int): Boolean
    suspend fun getCommentByID(id: Int): Comment?
    suspend fun getCommentsByIssueID(id: Int): List<Comment>
    suspend fun getCommentsByUserID(id: Int): List<Comment>
    suspend fun createComment(comment: CommentEntryRequest): Comment?
    suspend fun editComment(comment: CommentEntryRequest): Comment?
}