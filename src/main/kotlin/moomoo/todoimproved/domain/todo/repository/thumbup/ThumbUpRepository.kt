package moomoo.todoimproved.domain.todo.repository.thumbup

import moomoo.todoimproved.domain.todo.model.thumbup.ThumbUp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThumbUpRepository : JpaRepository<ThumbUp, Long> {
    fun findByUserIdAndTodoId(userId: Long, todoId: Long): ThumbUp?
}