package edu.kitt.orm

import edu.kitt.domainmodel.Comment
import edu.kitt.domainmodel.User

class InMemoryCommentRepository : CommentRepository {
    private val users = listOf<User>(
        User(1, "matteo@gmail.com", "cardisk"),
        User(2, "leonardo@gmail.com", "spectrev333"),
        User(3, "mirco@gmail.com", "mircocaneschi"),
    )

    private val comments = mutableListOf<Comment>(
        Comment(1, users[0], "this is a comment", 1),
        Comment(2, users[1], "this is a comment", 1),
        Comment(3, users[2], "this is a comment", 1),
    )

    override fun getCommentsByIssueID(id: Int): List<Comment> {
        return comments.filter { it.issueID == id }
    }

    override fun getCommentsByUserID(id: Int): List<Comment> {
        return comments.filter { it.author.id == id }
    }

}