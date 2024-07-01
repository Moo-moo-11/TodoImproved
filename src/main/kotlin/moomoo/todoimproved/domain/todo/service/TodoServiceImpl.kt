package moomoo.todoimproved.domain.todo.service

import moomoo.todoimproved.domain.exception.AccessDeniedException
import moomoo.todoimproved.domain.exception.ModelNotFoundException
import moomoo.todoimproved.domain.todo.dto.CreateTodoRequest
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.dto.TodoResponseWithComments
import moomoo.todoimproved.domain.todo.dto.UpdateTodoRequest
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.todo.model.thumbup.ThumbUp
import moomoo.todoimproved.domain.todo.repository.TodoRepository
import moomoo.todoimproved.domain.todo.repository.thumbup.ThumbUpRepository
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.aop.StopWatch
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoServiceImpl(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository,
    private val thumbUpRepository: ThumbUpRepository
) : TodoService {

    override fun getTodoList(pageable: Pageable): Page<TodoResponse> {
        return todoRepository.findAllTodo(pageable)
    }

    @StopWatch
    @Transactional(readOnly = true)
    override fun getTodo(todoId: Long): TodoResponseWithComments {
        return todoRepository.findByIdOrNull(todoId)
            ?.let { TodoResponseWithComments.from(it, thumbUpRepository.thumbsUpCount(it.id!!)) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    override fun searchTodos(
        pageable: Pageable,
        title: String?,
        nickname: String?,
        isCompleted: Boolean?,
        daysAgo: Long?
    ): Page<TodoResponse> {
        return todoRepository.searchTodos(pageable, title, nickname, isCompleted, daysAgo)
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
            .let { TodoResponse.from(it, 0) }
    }

    @Transactional
    override fun updateTodo(userPrincipal: UserPrincipal, todoId: Long, request: UpdateTodoRequest): TodoResponse {
        return todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.apply { this.updateTodo(request.title, request.description) }
            ?.let { TodoResponse.from(it, thumbUpRepository.thumbsUpCount(it.id!!)) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    @Transactional
    override fun toggleTodo(userPrincipal: UserPrincipal, todoId: Long): TodoResponse {
        return todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.apply { this.toggleTodo() }
            ?.let { TodoResponse.from(it, thumbUpRepository.thumbsUpCount(it.id!!)) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    @Transactional
    override fun deleteTodo(userPrincipal: UserPrincipal, todoId: Long) {
        todoRepository.findByIdOrNull(todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Todo") }
            ?.let { todoRepository.deleteTodoWithThumbUpsAndComments(it.id!!) }
            ?: throw ModelNotFoundException("Todo", todoId)
    }

    override fun thumbUpTodo(userPrincipal: UserPrincipal, todoId: Long) {
        if (thumbUpRepository.existsByUserIdAndTodoId(userPrincipal.id, todoId))
            throw IllegalArgumentException("You've already given a thumbs up to this post")

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

    private fun ThumbUpRepository.thumbsUpCount(todoId: Long): Long {
        return thumbUpRepository.countByTodoId(todoId)
    }

}