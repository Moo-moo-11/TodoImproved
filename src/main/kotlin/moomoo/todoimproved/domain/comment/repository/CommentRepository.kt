package moomoo.todoimproved.domain.comment.repository

import moomoo.todoimproved.domain.comment.model.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByIdAndTodoId(id: Long, todoId: Long): Comment?
    fun findAllByTodoId(todoId: Long): List<Comment>
}