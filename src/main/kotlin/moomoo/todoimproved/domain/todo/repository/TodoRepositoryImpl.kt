package moomoo.todoimproved.domain.todo.repository

import com.querydsl.core.types.dsl.BooleanExpression
import moomoo.todoimproved.domain.comment.model.QComment
import moomoo.todoimproved.domain.todo.model.QTodo
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.todo.model.thumbup.QThumbUp
import moomoo.todoimproved.domain.user.model.QUser
import moomoo.todoimproved.infra.querydsl.QueryDslSupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class TodoRepositoryImpl : CustomTodoRepository, QueryDslSupport() {
    private val todo = QTodo.todo
    private val comment = QComment.comment
    private val user = QUser.user
    private val thumbUp = QThumbUp.thumbUp

    override fun findAllTodo(pageable: Pageable): Page<Todo> {
        val totalCount = queryFactory.select(todo.count()).from(todo).fetchOne() ?: 0L

        val todoList = queryFactory.selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .leftJoin(todo.thumbUps, thumbUp).fetchJoin()
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .distinct()
            .fetch()

        return PageImpl(todoList, pageable, totalCount)
    }

    override fun searchTodos(
        pageable: Pageable,
        title: String?,
        nickname: String?,
        isCompleted: Boolean?,
        daysAgo: Long?
    ): Page<Todo> {
        val totalCount = queryFactory.select(todo.count())
            .from(todo)
            .where(
                titleContains(title),
                nicknameEq(nickname),
                isCompletedEq(isCompleted),
                createdAfter(daysAgo)
            )
            .fetchOne() ?: 0L

        val sort = if (pageable.sort.isSorted) {
            when (pageable.sort.first()?.property) {
                "createdAt" -> if (pageable.sort.first()?.isAscending == true) todo.createdAt.asc() else todo.createdAt.desc()
                "title" -> if (pageable.sort.first()?.isAscending == true) todo.title.asc() else todo.title.desc()
                else -> todo.createdAt.desc()
            }
        } else {
            todo.createdAt.desc()
        }

        val todoList = queryFactory.selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .leftJoin(todo.thumbUps, thumbUp).fetchJoin()
            .where(
                titleContains(title),
                nicknameEq(nickname),
                isCompletedEq(isCompleted),
                createdAfter(daysAgo)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .distinct()
            .orderBy(sort)
            .fetch()

        return PageImpl(todoList, pageable, totalCount)
    }

    override fun deleteTodoWithThumbUpsAndComments(todoId: Long) {
        queryFactory.delete(thumbUp).where(thumbUp.todo.id.eq(todoId)).execute()

        queryFactory.delete(comment).where(comment.todo.id.eq(todoId)).execute()

        queryFactory.delete(todo).where(todo.id.eq(todoId)).execute()
    }

    private fun titleContains(title: String?): BooleanExpression? {
        return if (title != null) {
            todo.title.containsIgnoreCase(title)
        } else {
            null
        }
    }

    private fun nicknameEq(nickname: String?): BooleanExpression? {
        return if (nickname != null) {
            todo.user.nickname.eq(nickname)
        } else {
            null
        }
    }

    private fun isCompletedEq(isCompleted: Boolean?): BooleanExpression? {
        return if (isCompleted != null) {
            todo.isCompleted.eq(isCompleted)
        } else {
            null
        }
    }

    private fun createdAfter(daysAgo: Long?): BooleanExpression? {
        return if (daysAgo != null) {
            todo.createdAt.after(LocalDateTime.now().minusDays(daysAgo))
        } else {
            null
        }
    }
}