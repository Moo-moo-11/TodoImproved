package moomoo.todoimproved.domain.todo.service

import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import org.springframework.stereotype.Service

@Service
class TodoServiceImpl : TodoService {

    override fun getTodoList(): List<TodoResponse> {
        TODO()
    }

    override fun getTodo(todoId: Long): TodoResponse {
        TODO("Not yet implemented")
    }

    override fun createTodo(request: CreateTodoRequest): TodoResponse {
        TODO("Not yet implemented")
    }

    override fun updateTodo(todoId: Long, request: UpdateTodoRequest): TodoResponse {
        TODO("Not yet implemented")
    }

    override fun thumbUpTodo(todoId: Long) {
        TODO("Not yet implemented")
    }

    override fun cancelThumbUpTodo(todoId: Long) {
        TODO("Not yet implemented")
    }
}