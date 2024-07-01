package moomoo.todoimproved.domain.comment.service

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.comment.dto.CreateCommentRequest
import moomoo.todoimproved.domain.comment.dto.UpdateCommentRequest
import moomoo.todoimproved.domain.comment.model.Comment
import moomoo.todoimproved.domain.comment.repository.CommentRepository
import moomoo.todoimproved.domain.exception.AccessDeniedException
import moomoo.todoimproved.domain.exception.ModelNotFoundException
import moomoo.todoimproved.domain.todo.repository.TodoRepository
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentServiceImpl(
    private val todoRepository: TodoRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) : CommentService {

    override fun getComment(todoId: Long, commentId: Long): CommentResponse {
        return commentRepository.findByIdAndTodoId(todoId, commentId)
            ?.let { CommentResponse.from(it) }
            ?: throw ModelNotFoundException("Comment", commentId)
    }

    override fun createComment(
        userPrincipal: UserPrincipal,
        todoId: Long,
        request: CreateCommentRequest
    ): CommentResponse {
        val todo = todoRepository.findByIdOrNull(todoId) ?: throw ModelNotFoundException("Todo", todoId)

        val user =
            userRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException("User", userPrincipal.id)

        return Comment(content = request.content, todo = todo, user = user)
            .let { commentRepository.save(it) }
            .let { CommentResponse.from(it) }
    }

    @Transactional
    override fun updateComment(
        userPrincipal: UserPrincipal,
        todoId: Long,
        commentId: Long,
        request: UpdateCommentRequest
    ): CommentResponse {
        return commentRepository.findByIdAndTodoId(commentId, todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Comment") }
            ?.apply { this.updateComment(request.content) }
            ?.let { CommentResponse.from(it) }
            ?: throw ModelNotFoundException("Comment", commentId)
    }

    override fun deleteComment(userPrincipal: UserPrincipal, todoId: Long, commentId: Long) {
        commentRepository.findByIdAndTodoId(commentId, todoId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You do not own this Comment") }
            ?.let { commentRepository.delete(it) }
            ?: throw ModelNotFoundException("Comment", commentId)
    }
}