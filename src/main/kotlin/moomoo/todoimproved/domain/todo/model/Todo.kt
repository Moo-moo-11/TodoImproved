package moomoo.todoimproved.domain.todo.model

import jakarta.persistence.*
import moomoo.todoimproved.domain.common.BaseEntity
import moomoo.todoimproved.domain.user.model.User

@Entity
class Todo(
    var title: String,

    var description: String,

    var isCompleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    var user: User
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


}