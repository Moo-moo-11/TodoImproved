package moomoo.todoimproved.domain.todo.controller

import jakarta.validation.Valid
import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.TodoResponseWithComments
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import moomoo.todoimproved.domain.todo.service.TodoService
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService
) {

    @GetMapping
    fun getTodoList(
        @PageableDefault(size = 12, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<TodoResponse>> {
        return ResponseEntity
            .ok(todoService.getTodoList(pageable))
    }

    @GetMapping("/{todoId}")
    fun getTodo(@PathVariable todoId: Long): ResponseEntity<TodoResponseWithComments> {
        return ResponseEntity
            .ok(todoService.getTodo(todoId))
    }

    @GetMapping("/search")
    fun searchTodos(
        @PageableDefault(size = 12, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable,
        @RequestParam title: String?,
        @RequestParam nickname: String?,
        @RequestParam isCompleted: Boolean?,
        @RequestParam daysAgo: Long?
    ): ResponseEntity<Page<TodoResponse>> {
        return ResponseEntity
            .ok(todoService.searchTodos(pageable, title, nickname, isCompleted, daysAgo))
    }

    @PostMapping
    fun createTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @Valid @RequestBody request: CreateTodoRequest
    ): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(todoService.createTodo(userPrincipal, request))
    }

    @PutMapping("/{todoId}")
    fun updateTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable todoId: Long,
        @Valid @RequestBody request: UpdateTodoRequest
    ): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .ok(todoService.updateTodo(userPrincipal, todoId, request))
    }

    @PatchMapping("/{todoId}")
    fun toggleTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable todoId: Long
    ): ResponseEntity<TodoResponse> {
        return ResponseEntity
            .ok(todoService.toggleTodo(userPrincipal, todoId))
    }

    @DeleteMapping("/{todoId}")
    fun deleteTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable todoId: Long
    ): ResponseEntity<TodoResponse> {
        todoService.deleteTodo(userPrincipal, todoId)
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build()
    }

    @PostMapping("/{todoId}/thumb-up")
    fun thumbUpTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable todoId: Long
    ): ResponseEntity<Unit> {
        todoService.thumbUpTodo(userPrincipal, todoId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{todoId}/thumb-up")
    fun cancelThumbUpTodo(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable todoId: Long
    ): ResponseEntity<Unit> {
        todoService.cancelThumbUpTodo(userPrincipal, todoId)
        return ResponseEntity.ok().build()
    }

}