package edu.kitt.orm.inmemory

import edu.kitt.domainmodel.Comment
import edu.kitt.orm.CommentRepository
import edu.kitt.orm.UserRepository
import edu.kitt.orm.entries.CommentEntry
import edu.kitt.orm.requests.CommentEntryRequest

class InMemoryCommentRepository(val userRepository: UserRepository) : CommentRepository {
    private val comments = mutableListOf(
        CommentEntry(1, 1, "this is a comment", 1),
        CommentEntry(2, 2, "this is a comment", 1),
        CommentEntry(3, 3, "this is a comment", 1),
    )

    override suspend fun removeCommentByID(id: Int): Boolean {
        return comments.removeIf { it.id == id }
    }

    override suspend fun getCommentsByIssueID(id: Int): List<Comment> {
        return comments.filter { it.issueID == id }.map {
            val user = userRepository.getUserByID(it.author);
            // FIXME: this will throw if user is invalid
            Comment(it.id, user!!, it.text)
        }
    }

    override suspend fun getCommentsByUserID(id: Int): List<Comment> {
        return comments.filter { it.author == id }.map {
            val user = userRepository.getUserByID(it.author);
            // FIXME: this will throw if user is invalid
            Comment(it.id, user!!, it.text)
        }
    }

    override suspend fun createComment(comment: CommentEntryRequest): Comment? {
        val new = CommentEntry(
            id = comments.last().id + 1,
            author = comment.author?: return null,
            text = comment.text?: return null,
            issueID = comment.issueID?: return null
        )

        if (comments.add(new)) {
            return Comment(new.id, userRepository.getUserByID(new.author)!!, new.text)
        }
        return null
    }

    override suspend fun editComment(comment: CommentEntryRequest): Comment? {
        val stored = comments.find { it.id == comment.id && it.issueID == comment.issueID } ?: return null
        val edited = stored.copy(
            text = comment.text ?: stored.text
        )

        // this moves the edited entry at the bottom, use index if its a problem
        comments.remove(stored)
        comments.add(edited)

        return Comment(edited.id, userRepository.getUserByID(edited.author)!!, edited.text)
    }
}