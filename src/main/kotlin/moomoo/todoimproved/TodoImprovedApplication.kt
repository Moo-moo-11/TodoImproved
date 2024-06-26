package moomoo.todoimproved

import moomoo.todoimproved.infra.security.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableConfigurationProperties(JwtProperties::class)
@EnableJpaAuditing
@SpringBootApplication
class TodoImprovedApplication

fun main(args: Array<String>) {
    runApplication<TodoImprovedApplication>(*args)
}
