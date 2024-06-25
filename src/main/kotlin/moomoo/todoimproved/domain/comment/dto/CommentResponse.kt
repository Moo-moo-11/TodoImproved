package moomoo.todoimproved.domain.comment.dto

data class CommentResponse(
    val id: Long,
    val comment: String,
    val nickname: String
)