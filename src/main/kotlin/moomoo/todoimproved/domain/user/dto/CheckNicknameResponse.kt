package moomoo.todoimproved.domain.user.dto

data class CheckNicknameResponse(
    val message: String
) {
    companion object {
        fun from(message: String): CheckNicknameResponse =
            CheckNicknameResponse(
                message = message
            )
    }

}
