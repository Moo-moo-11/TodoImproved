package moomoo.todoimproved.domain.todo.dto

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.todo.model.Todo
import java.time.LocalDateTime

data class TodoResponse(
    val id: Long,
    val nickname: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val thumbUpCount: Long,
    val createdAt: LocalDateTime,
    val comments: List<CommentResponse>
) {
    companion object {
        fun from(todo: Todo) = TodoResponse(
            id = todo.id!!,
            nickname = todo.user.nickname,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            thumbUpCount = todo.thumbUps.size.toLong(),
            createdAt = todo.createdAt,
            comments = todo.comments.map { CommentResponse.from(it) }
        )
    }
}
