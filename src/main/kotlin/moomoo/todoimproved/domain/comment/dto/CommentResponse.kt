package moomoo.todoimproved.domain.comment.dto

import moomoo.todoimproved.domain.comment.model.Comment
import java.time.LocalDateTime

data class CommentResponse(
    val id: Long,
    val content: String,
    val nickname: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(comment: Comment) = CommentResponse(
            id = comment.id!!,
            content = comment.content,
            nickname = comment.user.nickname,
            createdAt = comment.createdAt
        )
    }
}