package moomoo.todoimproved.domain.user.dto

data class LoginRequest(
    val nickname: String,
    val password: String
)
