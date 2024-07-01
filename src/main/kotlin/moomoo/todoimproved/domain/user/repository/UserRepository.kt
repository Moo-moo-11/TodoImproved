package moomoo.todoimproved.domain.user.repository

import moomoo.todoimproved.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByNickname(nickname: String): Boolean
    fun findByNickname(nickname: String): User?
}