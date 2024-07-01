package moomoo.todoimproved.domain.user.dto

import jakarta.validation.constraints.Pattern
import moomoo.todoimproved.domain.user.model.User
import org.springframework.security.crypto.password.PasswordEncoder

data class SignUpRequest(
    @field:Pattern(
        regexp = """[a-zA-Z\d]{3,30}""",
        message = "닉네임은 최소 3자 이상 30자 이하, 알파벳 대소문자, 숫자로 구성되어야 합니다"
    )
    val nickname: String,
    @field:Pattern(
        regexp = """[a-zA-Z\d!@#$?&%^*+=-]{4,20}""",
        message = "비밀번호는 최소 4자 이상 20자 이하, 알파벳 대소문자, 숫자로 구성되어야 합니다"
    )
    val password: String,
    val confirmPassword: String,
    val name: String,
    val profileImageUrl: String?
) {
    fun toEntity(passwordEncoder: PasswordEncoder): User {
        if (password != confirmPassword) throw IllegalArgumentException("비밀번호 확인이 비밀번호와 일치하지 않습니다")

        if (password.contains(nickname)) throw IllegalArgumentException("비밀번호는 닉네임을 포함할 수 없습니다")

        return User(
            nickname = nickname,
            password = passwordEncoder.encode(password),
            name = name,
            profileImageUrl = profileImageUrl
        )
    }
}
