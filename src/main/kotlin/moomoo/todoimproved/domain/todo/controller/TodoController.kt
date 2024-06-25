package moomoo.todoimproved.domain.todo.controller

import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import moomoo.todoimproved.domain.todo.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService
) {

    @GetMapping
    fun getTodoList(): ResponseEntity<List<TodoResponse>> {
        return ResponseEntity
            .ok(todoService.getTodoList())
    }

    @GetMapping("/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .ok(todoService.getTodo(todoId))
    }

    @PostMapping
    fun createTodo(@RequestBody request: CreateTodoRequest): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(todoService.createTodo(request))
    }

    @PutMapping("/{todoId}")
    fun updateTodo(@PathVariable todoId: Long, @RequestBody request: UpdateTodoRequest): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .ok(todoService.updateTodo(todoId, request))
    }

    @DeleteMapping("/{todoId}")
    fun deleteTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }

    @PostMapping("/{todoId}/thumb-up")
    fun thumbUpTodo(@PathVariable todoId: Long): ResponseEntity<Unit> {
        todoService.thumbUpTodo(todoId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{todoId}/thumb-up")
    fun cancelThumbUpTodo(@PathVariable todoId: Long): ResponseEntity<Unit> {
        todoService.cancelThumbUpTodo(todoId)
        return ResponseEntity.ok().build()
    }

}