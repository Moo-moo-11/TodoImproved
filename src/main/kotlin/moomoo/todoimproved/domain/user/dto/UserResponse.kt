package moomoo.todoimproved.domain.user.dto

import moomoo.todoimproved.domain.user.model.User

data class UserResponse(
    val id: Long,
    val nickname: String,
    val name: String,
    val profileImageUrl: String?
) {
    companion object {
        fun from(user: User): UserResponse =
            UserResponse(
                id = user.id!!,
                nickname = user.nickname,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )
    }
}

