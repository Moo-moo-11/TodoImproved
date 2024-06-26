package moomoo.todoimproved.domain.todo.dto

import jakarta.validation.constraints.Size

data class CreateTodoRequest(
    @field: Size(min = 1, max = 500, message = "제목은 1~500글자 이어야합니다")
    val title: String,
    @field: Size(min = 1, max = 5000, message = "제목은 1~5000글자 이어야합니다")
    val description: String
)
