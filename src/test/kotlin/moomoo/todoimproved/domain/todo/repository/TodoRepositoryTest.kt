package moomoo.todoimproved.domain.todo.repository

import io.kotest.matchers.collections.shouldBeSortedBy
import io.kotest.matchers.collections.shouldBeSortedDescendingBy
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import moomoo.todoimproved.domain.comment.model.Comment
import moomoo.todoimproved.domain.comment.repository.CommentRepository
import moomoo.todoimproved.domain.todo.model.Todo
import moomoo.todoimproved.domain.todo.model.thumbup.ThumbUp
import moomoo.todoimproved.domain.todo.repository.thumbup.ThumbUpRepository
import moomoo.todoimproved.domain.user.model.User
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.querydsl.QueryDslSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslSupport::class])
@ActiveProfiles("test")
class TodoRepositoryTest @Autowired constructor(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository,
    private val thumbUpRepository: ThumbUpRepository,
    private val commentRepository: CommentRepository
) {
    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Test
    fun `전체 Todo 조회시, 조회된 결과가 10개, PageSize 6일 때 0Page 결과 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.findAllTodo(Pageable.ofSize(6))

        // THEN
        result.content.size shouldBe 6
        result.isLast shouldBe false
        result.totalPages shouldBe 2
        result.number shouldBe 0
        result.totalElements shouldBe 10
    }

    @Test
    fun `전체 Todo 조회시, 조회된 결과가 10개, PageSize 6일 때 1Page 결과 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.findAllTodo(PageRequest.of(1, 6))

        // THEN
        result.content.size shouldBe 4
        result.isLast shouldBe true
        result.totalPages shouldBe 2
        result.number shouldBe 1
        result.totalElements shouldBe 10
    }

    @Test
    fun `전체 Todo 조회시 좋아요 개수가 제대로 나오는지, createdAt 오름차순으로 정렬되는지 결과 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        val thumbUps = createDefaultThumbUps(users, todos)
            .also { thumbUpRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.findAllTodo(Pageable.ofSize(10))

        // THEN
        result.content.first { it.title == "1번 투두" }.thumbUpCount shouldBe 5L
        result.content.first { it.title == "2번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "3번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "4번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "5번 투두" }.thumbUpCount shouldBe 3L
        result.content.first { it.title == "6번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "7번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "8번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "9번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "10번 투두" }.thumbUpCount shouldBe 1L

        result.content shouldBeSortedDescendingBy { it.createdAt }
    }

    @Test
    fun `검색 할 때 좋아요 개수가 제대로 나오는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        val thumbUps = createDefaultThumbUps(users, todos)
            .also { thumbUpRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(10), null, null, null, null)

        // THEN
        result.content.first { it.title == "1번 투두" }.thumbUpCount shouldBe 5L
        result.content.first { it.title == "2번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "3번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "4번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "5번 투두" }.thumbUpCount shouldBe 3L
        result.content.first { it.title == "6번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "7번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "8번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "9번 투두" }.thumbUpCount shouldBe 0L
        result.content.first { it.title == "10번 투두" }.thumbUpCount shouldBe 1L

        result.content shouldBeSortedDescendingBy { it.createdAt }
    }

    @Test
    fun `검색 할 때 title에 검색 조건을 입력했을 때 검색 결과가 제대로 나오는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(6), "3번", null, null, null)
        val result2 = todoRepository.searchTodos(Pageable.ofSize(6), "투두", null, null, null)

        // THEN
        result.content[0].title shouldContain "3번"
        result.content[0].thumbUpCount shouldBe 0L
        result.content.size shouldBe 1
        result.isLast shouldBe true
        result.totalPages shouldBe 1
        result.number shouldBe 0
        result.totalElements shouldBe 1

        result2.content.size shouldBe 6
        result2.isLast shouldBe false
        result2.totalPages shouldBe 2
        result2.number shouldBe 0
        result2.totalElements shouldBe 10
    }

    @Test
    fun `검색 할 때 name에 검색 조건을 입력했을 때 검색 결과가 제대로 나오는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(6), null, "유저1번이름", null, null)
        val result2 = todoRepository.searchTodos(Pageable.ofSize(6), null, "유저2번이름", null, null)

        // THEN
        result.content[0].title shouldBe "2번 투두"
        result.content[1].title shouldBe "1번 투두"
        result.content.size shouldBe 2
        result.isLast shouldBe true
        result.totalPages shouldBe 1
        result.number shouldBe 0
        result.totalElements shouldBe 2

        result2.content[0].title shouldBe "4번 투두"
        result2.content[1].title shouldBe "3번 투두"
        result2.content.size shouldBe 2
        result2.isLast shouldBe true
        result2.totalPages shouldBe 1
        result2.number shouldBe 0
        result2.totalElements shouldBe 2
    }

    @Test
    fun `검색 할 때 isCompleted에만 검색 조건을 입력했을 때 검색 결과가 제대로 나오는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(6), null, null, true, null)
        val result2 = todoRepository.searchTodos(Pageable.ofSize(6), null, null, false, null)

        // THEN
        result.content[0].title shouldBe "10번 투두"
        result.content[1].title shouldBe "9번 투두"
        result.content[2].title shouldBe "8번 투두"
        result.content[3].title shouldBe "7번 투두"
        result.content[4].title shouldBe "6번 투두"
        result.content.size shouldBe 5
        result.isLast shouldBe true
        result.totalPages shouldBe 1
        result.number shouldBe 0
        result.totalElements shouldBe 5

        result2.content[0].title shouldBe "5번 투두"
        result2.content[1].title shouldBe "4번 투두"
        result2.content[2].title shouldBe "3번 투두"
        result2.content[3].title shouldBe "2번 투두"
        result2.content[4].title shouldBe "1번 투두"
        result2.content.size shouldBe 5
        result2.isLast shouldBe true
        result2.totalPages shouldBe 1
        result2.number shouldBe 0
        result2.totalElements shouldBe 5
    }

    @Test
    fun `검색 할 때 daysAgo에만 검색 조건을 입력했을 때 검색 결과가 제대로 나오는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        val now = LocalDateTime.now()

        // 1~10일 전 날짜로 강제 업데이트
        for (i in 0..9) {

            val randomDate = now.minusDays(i.toLong())

            entityManager.createNativeQuery("UPDATE Todo SET created_at = ?1 WHERE title = ?2")
                .setParameter(1, randomDate)
                .setParameter(2, "${i}번 투두")
                .executeUpdate()
        }

        entityManager.clear()

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(10), null, null, null, 2)
        val result2 = todoRepository.searchTodos(Pageable.ofSize(10), null, null, null, 5)
        val result3 = todoRepository.searchTodos(Pageable.ofSize(10), null, null, null, 15)

        // THEN
        result.content.forEach {
            it.createdAt shouldBeAfter now.minusDays(2L)
            it.createdAt shouldNotBeBefore now.minusDays(2L)
        }
        result.content.size shouldBe 2

        result2.content.forEach {
            it.createdAt shouldBeAfter now.minusDays(5L)
            it.createdAt shouldNotBeBefore now.minusDays(5L)
        }
        result2.content.size shouldBe 5

        result3.content.size shouldBe 10
        result3.isLast shouldBe true
        result3.totalPages shouldBe 1
        result3.number shouldBe 0
        result3.totalElements shouldBe 10
    }

    @Test
    fun `검색 할 때 title 오름차순, 내림차순으로 정렬을 원할 경우 잘 정렬되는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result =
            todoRepository.searchTodos(PageRequest.of(0, 6, Sort.Direction.ASC, "title"), null, null, null, null)
        val result2 =
            todoRepository.searchTodos(PageRequest.of(0, 6, Sort.Direction.DESC, "title"), null, null, null, null)

        // THEN
        result.content shouldBeSortedBy { it.title }
        result.content.size shouldBe 6
        result.isLast shouldBe false
        result.totalPages shouldBe 2
        result.number shouldBe 0
        result.totalElements shouldBe 10

        result2.content shouldBeSortedDescendingBy { it.title }
        result2.content.size shouldBe 6
        result2.isLast shouldBe false
        result2.totalPages shouldBe 2
        result2.number shouldBe 0
        result2.totalElements shouldBe 10
    }

    @Test
    fun `검색 할 때 기본값으로 createdAt 내림차순으로 잘 정렬되는지, 오름차순을 넣어주면 오름차순으로 잘 정렬되는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        // WHEN
        val result =
            todoRepository.searchTodos(PageRequest.of(1, 6), null, null, null, null)
        val result2 =
            todoRepository.searchTodos(PageRequest.of(1, 6, Sort.Direction.ASC, "createdAt"), null, null, null, null)

        // THEN
        result.content shouldBeSortedDescendingBy { it.createdAt }
        result.content.size shouldBe 4
        result.isLast shouldBe true
        result.totalPages shouldBe 2
        result.number shouldBe 1
        result.totalElements shouldBe 10

        result2.content shouldBeSortedBy { it.createdAt }
        result2.content.size shouldBe 4
        result2.isLast shouldBe true
        result2.totalPages shouldBe 2
        result2.number shouldBe 1
        result2.totalElements shouldBe 10
    }

    @Test
    fun `투두 삭제 할 때 아래에 딸린 좋아요, 댓글 모두 잘 삭제되는지 확인`() {
        // GIVEN
        val users = createDefaultUsers()
            .also { userRepository.saveAllAndFlush(it) }

        val todos = createDefaultTodos(users)
            .also { todoRepository.saveAllAndFlush(it) }

        val thumbUps = createDefaultThumbUps(users, todos)
            .also { thumbUpRepository.saveAllAndFlush(it) }

        val comments = createDefaultComments(users, todos)
            .also { commentRepository.saveAllAndFlush(it) }
        
        entityManager.clear()

        // WHEN
        todoRepository.deleteTodoWithThumbUpsAndComments(todos[0].id!!)

        val todoResult = todoRepository.findByIdOrNull(todos[0].id!!)
        val commentResult = commentRepository.findAllByTodoId(todos[0].id!!)
        val thumbUpResult = thumbUpRepository.findAllByTodoId(todos[0].id!!)
        val commentResult2 = commentRepository.findAllByTodoId(todos[1].id!!)

        // THEN
        todoResult shouldBe null

        commentResult.size shouldBe 0
        commentResult2.size shouldBe 2

        thumbUpResult.size shouldBe 0
    }


    private fun createDefaultUsers(): List<User> {
        return listOf(
            User(nickname = "유저1번", password = "비밀번호", name = "유저1번이름"),
            User(nickname = "유저2번", password = "비밀번호", name = "유저2번이름"),
            User(nickname = "유저3번", password = "비밀번호", name = "유저3번이름"),
            User(nickname = "유저4번", password = "비밀번호", name = "유저4번이름"),
            User(nickname = "유저5번", password = "비밀번호", name = "유저5번이름")
        )
    }

    private fun createDefaultTodos(users: List<User>): List<Todo> {
        return listOf(
            Todo(title = "1번 투두", description = "1번 내용", user = users[0]),
            Todo(title = "2번 투두", description = "2번 내용", user = users[0]),
            Todo(title = "3번 투두", description = "3번 내용", user = users[1]),
            Todo(title = "4번 투두", description = "4번 내용", user = users[1]),
            Todo(title = "5번 투두", description = "5번 내용", user = users[2]),
            Todo(title = "6번 투두", description = "6번 내용", user = users[2], isCompleted = true),
            Todo(title = "7번 투두", description = "7번 내용", user = users[3], isCompleted = true),
            Todo(title = "8번 투두", description = "8번 내용", user = users[3], isCompleted = true),
            Todo(title = "9번 투두", description = "9번 내용", user = users[4], isCompleted = true),
            Todo(title = "10번 투두", description = "10번 내용", user = users[4], isCompleted = true)
        )
    }

    private fun createDefaultThumbUps(users: List<User>, todos: List<Todo>): List<ThumbUp> {
        return listOf(
            ThumbUp(user = users[0], todo = todos[0]),
            ThumbUp(user = users[1], todo = todos[0]),
            ThumbUp(user = users[2], todo = todos[0]),
            ThumbUp(user = users[3], todo = todos[0]),
            ThumbUp(user = users[4], todo = todos[0]),
            ThumbUp(user = users[0], todo = todos[4]),
            ThumbUp(user = users[1], todo = todos[4]),
            ThumbUp(user = users[2], todo = todos[4]),
            ThumbUp(user = users[0], todo = todos[9])
        )
    }

    private fun createDefaultComments(users: List<User>, todos: List<Todo>): List<Comment> {
        return listOf(
            Comment(content = "1번 투두에 유저 1번이 단 댓글", todo = todos[0], user = users[0]),
            Comment(content = "1번 투두에 유저 2번이 단 댓글", todo = todos[0], user = users[1]),
            Comment(content = "1번 투두에 유저 3번이 단 댓글", todo = todos[0], user = users[2]),
            Comment(content = "2번 투두에 유저 4번이 단 댓글", todo = todos[1], user = users[3]),
            Comment(content = "2번 투두에 유저 5번이 단 댓글", todo = todos[1], user = users[4]),
            Comment(content = "3번 투두에 유저 1번이 단 댓글", todo = todos[2], user = users[0]),
            Comment(content = "4번 투두에 유저 2번이 단 댓글", todo = todos[3], user = users[1]),
            Comment(content = "5번 투두에 유저 3번이 단 댓글", todo = todos[4], user = users[2]),
            Comment(content = "6번 투두에 유저 4번이 단 댓글", todo = todos[5], user = users[3]),
            Comment(content = "7번 투두에 유저 5번이 단 댓글", todo = todos[6], user = users[4]),
            Comment(content = "8번 투두에 유저 1번이 단 댓글", todo = todos[7], user = users[0]),
            Comment(content = "9번 투두에 유저 2번이 단 댓글", todo = todos[8], user = users[1])
        )
    }

    companion object {

        private val DEFAULT_USER_LIST = listOf(
            User(nickname = "유저1번", password = "비밀번호", name = "유저1번이름"),
            User(nickname = "유저2번", password = "비밀번호", name = "유저2번이름"),
            User(nickname = "유저3번", password = "비밀번호", name = "유저3번이름"),
            User(nickname = "유저4번", password = "비밀번호", name = "유저4번이름"),
            User(nickname = "유저5번", password = "비밀번호", name = "유저5번이름")
        )

        private val DEFAULT_TODO_LIST = listOf(
            Todo(title = "1번 투두", description = "1번 내용", user = DEFAULT_USER_LIST[0]),
            Todo(title = "2번 투두", description = "2번 내용", user = DEFAULT_USER_LIST[0]),
            Todo(title = "3번 투두", description = "3번 내용", user = DEFAULT_USER_LIST[1]),
            Todo(title = "4번 투두", description = "4번 내용", user = DEFAULT_USER_LIST[1]),
            Todo(title = "5번 투두", description = "5번 내용", user = DEFAULT_USER_LIST[2]),
            Todo(title = "6번 투두", description = "6번 내용", user = DEFAULT_USER_LIST[2]),
            Todo(title = "7번 투두", description = "7번 내용", user = DEFAULT_USER_LIST[3]),
            Todo(title = "8번 투두", description = "8번 내용", user = DEFAULT_USER_LIST[3]),
            Todo(title = "9번 투두", description = "9번 내용", user = DEFAULT_USER_LIST[4]),
            Todo(title = "10번 투두", description = "10번 내용", user = DEFAULT_USER_LIST[4])
        )

        private val DEFAULT_COMMENT_LIST = listOf(
            Comment(content = "1번 투두에 유저 1번이 단 댓글", todo = DEFAULT_TODO_LIST[0], user = DEFAULT_USER_LIST[0]),
            Comment(content = "1번 투두에 유저 2번이 단 댓글", todo = DEFAULT_TODO_LIST[0], user = DEFAULT_USER_LIST[1]),
            Comment(content = "1번 투두에 유저 3번이 단 댓글", todo = DEFAULT_TODO_LIST[0], user = DEFAULT_USER_LIST[2]),
            Comment(content = "2번 투두에 유저 4번이 단 댓글", todo = DEFAULT_TODO_LIST[1], user = DEFAULT_USER_LIST[3]),
            Comment(content = "2번 투두에 유저 5번이 단 댓글", todo = DEFAULT_TODO_LIST[1], user = DEFAULT_USER_LIST[4]),
            Comment(content = "3번 투두에 유저 1번이 단 댓글", todo = DEFAULT_TODO_LIST[2], user = DEFAULT_USER_LIST[0]),
            Comment(content = "4번 투두에 유저 2번이 단 댓글", todo = DEFAULT_TODO_LIST[3], user = DEFAULT_USER_LIST[1]),
            Comment(content = "5번 투두에 유저 3번이 단 댓글", todo = DEFAULT_TODO_LIST[4], user = DEFAULT_USER_LIST[2]),
            Comment(content = "6번 투두에 유저 4번이 단 댓글", todo = DEFAULT_TODO_LIST[5], user = DEFAULT_USER_LIST[3]),
            Comment(content = "7번 투두에 유저 5번이 단 댓글", todo = DEFAULT_TODO_LIST[6], user = DEFAULT_USER_LIST[4]),
            Comment(content = "8번 투두에 유저 1번이 단 댓글", todo = DEFAULT_TODO_LIST[7], user = DEFAULT_USER_LIST[0]),
            Comment(content = "9번 투두에 유저 2번이 단 댓글", todo = DEFAULT_TODO_LIST[8], user = DEFAULT_USER_LIST[1])
        )

        private val DEFAULT_THUMB_UP_LIST = listOf(
            ThumbUp(user = DEFAULT_USER_LIST[0], todo = DEFAULT_TODO_LIST[0]),
            ThumbUp(user = DEFAULT_USER_LIST[1], todo = DEFAULT_TODO_LIST[0]),
            ThumbUp(user = DEFAULT_USER_LIST[2], todo = DEFAULT_TODO_LIST[0]),
            ThumbUp(user = DEFAULT_USER_LIST[3], todo = DEFAULT_TODO_LIST[0]),
            ThumbUp(user = DEFAULT_USER_LIST[4], todo = DEFAULT_TODO_LIST[0]),
            ThumbUp(user = DEFAULT_USER_LIST[0], todo = DEFAULT_TODO_LIST[4]),
            ThumbUp(user = DEFAULT_USER_LIST[1], todo = DEFAULT_TODO_LIST[4]),
            ThumbUp(user = DEFAULT_USER_LIST[2], todo = DEFAULT_TODO_LIST[4]),
            ThumbUp(user = DEFAULT_USER_LIST[0], todo = DEFAULT_TODO_LIST[9]),
        )
    }
}