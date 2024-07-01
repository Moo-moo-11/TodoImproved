package moomoo.todoimproved.infra.querydsl

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Configuration

@Configuration
abstract class QueryDslSupport {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    val queryFactory: JPAQueryFactory by lazy {
        JPAQueryFactory(entityManager)
    }
}