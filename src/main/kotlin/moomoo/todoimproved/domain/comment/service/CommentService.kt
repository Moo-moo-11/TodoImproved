package moomoo.todoimproved.domain.comment.service

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.comment.dto.CreateCommentRequest
import moomoo.todoimproved.domain.comment.dto.UpdateCommentRequest
import moomoo.todoimproved.infra.security.UserPrincipal

interface CommentService {
    fun getComment(todoId: Long, commentId: Long): CommentResponse
    fun createComment(userPrincipal: UserPrincipal, todoId: Long, request: CreateCommentRequest): CommentResponse
    fun updateComment(
        userPrincipal: UserPrincipal,
        todoId: Long,
        commentId: Long,
        request: UpdateCommentRequest
    ): CommentResponse

    fun deleteComment(userPrincipal: UserPrincipal, todoId: Long, commentId: Long)
}