package moomoo.todoimproved.domain.todo.repository

import moomoo.todoimproved.domain.todo.model.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomTodoRepository {
    fun findAllTodo(pageable: Pageable): Page<Todo>
    fun searchTodos(
        pageable: Pageable,
        title: String?,
        nickname: String?,
        isCompleted: Boolean?,
        daysAgo: Long?
    ): Page<Todo>

    fun deleteTodoWithThumbUpsAndComments(todoId: Long)
}