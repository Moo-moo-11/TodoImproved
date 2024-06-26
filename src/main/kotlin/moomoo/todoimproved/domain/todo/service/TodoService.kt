package moomoo.todoimproved.domain.todo.service

import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import moomoo.todoimproved.infra.security.UserPrincipal

interface TodoService {
    fun getTodoList(): List<TodoResponse>
    fun getTodo(todoId: Long): TodoResponse
    fun createTodo(userPrincipal: UserPrincipal, request: CreateTodoRequest): TodoResponse
    fun updateTodo(userPrincipal: UserPrincipal, todoId: Long, request: UpdateTodoRequest): TodoResponse
    fun toggleTodo(userPrincipal: UserPrincipal, todoId: Long): TodoResponse
    fun deleteTodo(userPrincipal: UserPrincipal, todoId: Long)
    fun thumbUpTodo(userPrincipal: UserPrincipal, todoId: Long)
    fun cancelThumbUpTodo(userPrincipal: UserPrincipal, todoId: Long)
}