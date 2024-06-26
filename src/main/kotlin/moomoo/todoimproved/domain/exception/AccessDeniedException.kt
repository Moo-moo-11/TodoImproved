package moomoo.todoimproved.domain.exception

data class AccessDeniedException(
    private val text: String
) : RuntimeException(
    "Access Denied: $text"
)
