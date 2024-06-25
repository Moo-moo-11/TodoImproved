package moomoo.todoimproved.domain.todo.service

import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest

interface TodoService {
    fun getTodoList(): List<TodoResponse>
    fun getTodo(todoId: Long): TodoResponse
    fun createTodo(request: CreateTodoRequest): TodoResponse
    fun updateTodo(todoId: Long, request: UpdateTodoRequest): TodoResponse
    fun thumbUpTodo(todoId: Long)
    fun cancelThumbUpTodo(todoId: Long)
}