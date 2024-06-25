package moomoo.todoimproved.domain.comment.service

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.comment.dto.CreateCommentRequest
import moomoo.todoimproved.domain.comment.dto.UpdateCommentRequest
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl : CommentService {
    override fun getCommentList(todoId: Long, commentId: Long): List<CommentResponse> {
        TODO("Not yet implemented")
    }

    override fun createComment(todoId: Long, request: CreateCommentRequest): CommentResponse {
        TODO("Not yet implemented")
    }

    override fun updateComment(todoId: Long, commentId: Long, request: UpdateCommentRequest): CommentResponse {
        TODO("Not yet implemented")
    }

    override fun deleteComment(todoId: Long, commentId: Long) {
        TODO("Not yet implemented")
    }
}