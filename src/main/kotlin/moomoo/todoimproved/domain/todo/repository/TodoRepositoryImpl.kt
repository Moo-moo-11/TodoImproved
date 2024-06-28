package moomoo.todoimproved.domain.todo.repository

import com.querydsl.core.types.dsl.BooleanExpression
import moomoo.todoimproved.domain.comment.model.QComment
import moomoo.todoimproved.domain.todo.dto.TodoResponse
import moomoo.todoimproved.domain.todo.model.QTodo
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

    override fun findAllTodo(pageable: Pageable): Page<TodoResponse> {
        val totalCount = queryFactory.select(todo.count()).from(todo).fetchOne() ?: 0L

        val todoList = queryFactory.selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(todo.createdAt.desc())
            .fetch()

        val countList = queryFactory
            .select(
                thumbUp.todo.id.count()
            )
            .from(todo)
            .leftJoin(thumbUp).on(thumbUp.todo.id.eq(todo.id))
            .groupBy(todo.id)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(todo.createdAt.desc())
            .fetch()

        val todoResponseList: List<TodoResponse> = todoList.zip(countList) { a, b -> TodoResponse.from(a, b) }

        return PageImpl(todoResponseList, pageable, totalCount)
    }

    override fun searchTodos(
        pageable: Pageable,
        title: String?,
        name: String?,
        isCompleted: Boolean?,
        daysAgo: Long?
    ): Page<TodoResponse> {
        val totalCount = queryFactory.select(todo.count())
            .from(todo)
            .where(
                titleContains(title),
                nameEq(name),
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
            .where(
                titleContains(title),
                nameEq(name),
                isCompletedEq(isCompleted),
                createdAfter(daysAgo)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(sort)
            .fetch()

        val countList = queryFactory
            .select(
                thumbUp.todo.id.count()
            )
            .from(todo)
            .leftJoin(thumbUp).on(thumbUp.todo.id.eq(todo.id))
            .where(
                titleContains(title),
                nameEq(name),
                isCompletedEq(isCompleted),
                createdAfter(daysAgo)
            )
            .groupBy(todo.id)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(sort)
            .fetch()

        val todoResponseList = todoList.zip(countList) { a, b -> TodoResponse.from(a, b) }

        return PageImpl(todoResponseList, pageable, totalCount)
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

    private fun nameEq(name: String?): BooleanExpression? {
        return if (name != null) {
            todo.user.name.eq(name)
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