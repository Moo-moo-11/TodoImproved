package moomoo.todoimproved.domain.todo.service

import moomoo.todoimproved.domain.exception.AccessDeniedException
import moomoo.todoimproved.domain.exception.ModelNotFoundException
import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.todo.model.thumbup.ThumbUp
import moomoo.todoimproved.domain.todo.repository.TodoRepository
import moomoo.todoimproved.domain.todo.repository.thumbup.ThumbUpRepository
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoServiceImpl(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository,
    private val thumbUpRepository: ThumbUpRepository
) : TodoService {

    override fun getTodoList(): List<TodoResponse> {
        return todoRepository.findAll().map { TodoResponse.from(it) }
    }

    override fun getTodo(todoId: Long): TodoResponse {
        return todoRepository.findByIdOrNull(todoId)
            ?.let { TodoResponse.from(it) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    override fun createTodo(userPrincipal: UserPrincipal, request: CreateTodoRequest): TodoResponse {
        val user =
            userRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException("User", userPrincipal.id)

        return Todo(
            title = request.title,
            description = request.description,
            user = user
        )
            .let { todoRepository.save(it) }
            .let { TodoResponse.from(it) }
    }

    @Transactional
    override fun updateTodo(userPrincipal: UserPrincipal, todoId: Long, request: UpdateTodoRequest): TodoResponse {
        return todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.apply { this.updateTodo(request.title, request.description) }
            ?.let { TodoResponse.from(it) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    @Transactional
    override fun toggleTodo(userPrincipal: UserPrincipal, todoId: Long): TodoResponse {
        return todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.apply { this.toggleTodo() }
            ?.let { TodoResponse.from(it) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    override fun deleteTodo(userPrincipal: UserPrincipal, todoId: Long) {
        todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.let { todoRepository.delete(it) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    override fun thumbUpTodo(userPrincipal: UserPrincipal, todoId: Long) {
        val user =
            userRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException("User", userPrincipal.id)
        val todo = todoRepository.findByIdOrNull(todoId) ?: throw ModelNotFoundException("Todo", todoId)

        ThumbUp(user = user, todo = todo)
            .let { thumbUpRepository.save(it) }
    }

    override fun cancelThumbUpTodo(userPrincipal: UserPrincipal, todoId: Long) {
        thumbUpRepository.findByUserIdAndTodoId(userPrincipal.id, todoId)
            ?.let { thumbUpRepository.delete(it) }
            ?: throw IllegalArgumentException("You've not given a thumbs up to this post, so it can't be canceled")
    }

}