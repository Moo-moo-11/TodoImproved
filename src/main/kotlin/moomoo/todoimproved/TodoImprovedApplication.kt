package moomoo.todoimproved

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class TodoImprovedApplication

fun main(args: Array<String>) {
    runApplication<TodoImprovedApplication>(*args)
}
