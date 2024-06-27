package moomoo.todoimproved.domain.todo.repository

import io.kotest.matchers.collections.shouldBeSortedBy
import io.kotest.matchers.collections.shouldBeSortedDescendingBy
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import moomoo.todoimproved.domain.todo.model.Todo
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
import org.springframework.test.context.ActiveProfiles


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslSupport::class])
@ActiveProfiles("test")
class TodoRepositoryTest @Autowired constructor(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository
) {

    @Test
    fun `조회된 결과가 10개, PageSize 6일 때 0Page 결과 확인`() {
        // GIVEN
        userRepository.saveAllAndFlush(DEFAULT_USER_LIST)
        todoRepository.saveAllAndFlush(DEFAULT_TODO_LIST)

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
    fun `검색 할 때 title에만 값을 입력했을 때 값이 제대로 나오는지 확인`() {
        // GIVEN
        userRepository.saveAllAndFlush(DEFAULT_USER_LIST)
        todoRepository.saveAllAndFlush(DEFAULT_TODO_LIST)

        // WHEN
        val result = todoRepository.searchTodos(Pageable.ofSize(6), "3번", null, null, null)
        val result2 = todoRepository.searchTodos(Pageable.ofSize(6), "투두", null, null, null)

        // THEN
        result.content[0].title shouldContain "3번"
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
    fun `검색 할 때 createdAt 오름차순으로 정렬되는지 확인`() {
        // GIVEN
        userRepository.saveAllAndFlush(DEFAULT_USER_LIST)
        todoRepository.saveAllAndFlush(DEFAULT_TODO_LIST)

        // WHEN
        val result =
            todoRepository.searchTodos(PageRequest.of(0, 6, Sort.Direction.ASC, "title"), null, null, null, null)
        val result2 =
            todoRepository.searchTodos(PageRequest.of(1, 6), null, null, null, null)
        // THEN
        result.content shouldBeSortedBy { it.title }
        result.content.size shouldBe 6
        result.isLast shouldBe false
        result.totalPages shouldBe 2
        result.number shouldBe 0
        result.totalElements shouldBe 10

        result2.content shouldBeSortedDescendingBy { it.createdAt }
        result2.content.size shouldBe 4
        result2.isLast shouldBe true
        result2.totalPages shouldBe 2
        result2.number shouldBe 1
        result2.totalElements shouldBe 10
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
    }
}