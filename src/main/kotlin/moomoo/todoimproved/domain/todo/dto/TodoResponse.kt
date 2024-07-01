package moomoo.todoimproved.domain.todo.dto

import moomoo.todoimproved.domain.todo.model.Todo
import java.time.LocalDateTime

data class TodoResponse(
    val id: Long,
    val name: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val thumbUpCount: Long,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(todo: Todo, count: Long) = TodoResponse(
            id = todo.id!!,
            name = todo.user.name,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            thumbUpCount = count,
            createdAt = todo.createdAt
        )
    }
}
