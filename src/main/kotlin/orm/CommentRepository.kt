package edu.kitt.orm

import edu.kitt.domainmodel.Comment

interface CommentRepository {
    fun getCommentsByIssueID(id: Int): List<Comment>
    fun getCommentsByUserID(id: Int): List<Comment>
}