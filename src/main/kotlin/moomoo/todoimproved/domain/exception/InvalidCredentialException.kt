package moomoo.todoimproved.domain.exception

data class InvalidCredentialException(
    val text: String
) : RuntimeException(
    "Invalid Credential: $text"
)
