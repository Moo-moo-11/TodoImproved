package moomoo.todoimproved.domain.user.repository

import moomoo.todoimproved.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>, CustomUserRepository {
    fun existsByNickname(nickname: String): Boolean
    fun findByNickname(nickname: String): User?
}