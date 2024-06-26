package moomoo.todoimproved.domain.comment.repository

import moomoo.todoimproved.domain.comment.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>, CustomCommentRepository {
    fun findByIdAndTodoId(id: Long, todoId: Long): Comment?
}