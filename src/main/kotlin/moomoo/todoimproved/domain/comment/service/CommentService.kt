package moomoo.todoimproved.domain.comment.service

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.comment.dto.CreateCommentRequest
import moomoo.todoimproved.domain.comment.dto.UpdateCommentRequest

interface CommentService {
    fun getCommentList(todoId: Long, commentId: Long): List<CommentResponse>
    fun createComment(todoId: Long, request: CreateCommentRequest): CommentResponse
    fun updateComment(todoId: Long, commentId: Long, request: UpdateCommentRequest): CommentResponse
    fun deleteComment(todoId: Long, commentId: Long)
}