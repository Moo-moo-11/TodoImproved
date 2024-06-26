package moomoo.todoimproved.domain.exception

class ModelNotFoundException(
    val model: String, val Id: Any
) : RuntimeException(
    "$model Not Found With Given Id: $Id"
)