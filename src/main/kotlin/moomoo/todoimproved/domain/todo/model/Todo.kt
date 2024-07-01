package moomoo.todoimproved.domain.todo.model

import jakarta.persistence.*
import moomoo.todoimproved.domain.comment.model.Comment
import moomoo.todoimproved.domain.common.BaseEntity
import moomoo.todoimproved.domain.user.model.User

@Entity
class Todo(
    var title: String,

    var description: String,

    var isCompleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User,

    @OneToMany(mappedBy = "todo", fetch = FetchType.LAZY)
    var comments: MutableList<Comment> = mutableListOf()
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateTodo(title: String, description: String) {
        this.title = title
        this.description = description
    }

    fun toggleTodo() {
        isCompleted = !isCompleted
    }

    fun checkPermission(userId: Long): Boolean {
        return user.id == userId
    }

}