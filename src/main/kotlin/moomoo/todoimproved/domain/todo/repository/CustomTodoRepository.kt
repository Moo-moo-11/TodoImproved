package moomoo.todoimproved.domain.todo.repository

import moomoo.todoimproved.domain.todo.dto.TodoResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomTodoRepository {
    fun findAllTodo(pageable: Pageable): Page<TodoResponse>
    fun searchTodos(
        pageable: Pageable,
        title: String?,
        name: String?,
        isCompleted: Boolean?,
        daysAgo: Long?
    ): Page<TodoResponse>

    fun deleteTodoWithThumbUpsAndComments(todoId: Long)
}