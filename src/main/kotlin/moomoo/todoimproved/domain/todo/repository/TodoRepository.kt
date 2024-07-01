package moomoo.todoimproved.domain.todo.repository

import moomoo.todoimproved.domain.todo.model.Todo
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long>, CustomTodoRepository