package moomoo.todoimproved.domain.comment.model

import jakarta.persistence.*
import moomoo.todoimproved.domain.common.BaseEntity
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.user.model.User

@Entity
class Comment(
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    var todo: Todo,

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateComment(content: String) {
        this.content = content
    }

    fun checkPermission(userId: Long): Boolean {
        return user.id == userId
    }
}