package moomoo.todoimproved.domain.exception

class ModelNotFoundException(
    val model: String, val Id: Long
) : RuntimeException(
    "$model Not Found With Given Id: $Id"
)