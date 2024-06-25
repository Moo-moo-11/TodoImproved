package moomoo.todoimproved.domain.todo.model.thumbup

import jakarta.persistence.*
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.user.model.User

@Entity
class ThumbUp(
    @ManyToOne
    var user: User,

    @ManyToOne
    var todo: Todo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}