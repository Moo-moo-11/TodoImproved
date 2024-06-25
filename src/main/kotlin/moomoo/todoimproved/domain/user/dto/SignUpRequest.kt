package moomoo.todoimproved.domain.user.dto

data class SignUpRequest(
    val nickname: String,
    val password: String,
    val confirmPassword: String,
    val profileImage: String?
)
