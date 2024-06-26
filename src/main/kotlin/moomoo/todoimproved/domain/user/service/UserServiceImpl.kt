package moomoo.todoimproved.domain.user.service

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import moomoo.todoimproved.domain.exception.AccessDeniedException
import moomoo.todoimproved.domain.exception.InvalidCredentialException
import moomoo.todoimproved.domain.exception.ModelNotFoundException
import moomoo.todoimproved.domain.user.dto.*
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.security.UserPrincipal
import moomoo.todoimproved.infra.security.jwt.JwtPlugin
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
) : UserService {

    override fun signUp(request: SignUpRequest): UserResponse {
        return request
            .also { if (userRepository.existsByNickname(it.nickname)) throw IllegalArgumentException("중복된 닉네임입니다") }
            .toEntity(passwordEncoder)
            .let { userRepository.save(it) }
            .let { UserResponse.from(it) }
    }

    override fun checkDuplicateNickname(request: CheckNicknameRequest): CheckNicknameResponse {
        return userRepository.existsByNickname(request.nickname)
            .let { if (it) "이미 존재하는 닉네임입니다" else "사용가능한 닉네임입니다" }
            .let { CheckNicknameResponse.from(it) }
    }

    override fun login(request: LoginRequest, response: HttpServletResponse): LoginResponse {
        val user =
            userRepository.findByNickname(request.nickname) ?: throw ModelNotFoundException("User", request.nickname)

        if (!passwordEncoder.matches(request.password, user.password))
            throw InvalidCredentialException("닉네임 또는 패스워드를 확인해주세요")

        val accessToken = jwtPlugin.generateAccessToken(user.id.toString(), user.nickname)

        val cookie = Cookie("accessToken", accessToken)
            .apply {
                path = "/"
                maxAge = 2 * 24 * 60 * 60
                isHttpOnly = true
            }

        response.addCookie(cookie)

        return LoginResponse(accessToken = accessToken)
    }

    override fun getUser(userPrincipal: UserPrincipal, userId: Long): UserResponse {
        return userRepository.findByIdOrNull(userId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You are not this User") }
            ?.let { UserResponse.from(it) }
            ?: throw ModelNotFoundException("User", userId)
    }

    @Transactional
    override fun updateUser(
        userPrincipal: UserPrincipal,
        userId: Long,
        request: UpdateUserProfileRequest
    ): UserResponse {
        return userRepository.findByIdOrNull(userId)
            ?.also { if (!it.checkPermission(userPrincipal.id)) throw AccessDeniedException("You are not this User") }
            ?.also { it.updateProfileImage(request.profileImageUrl) }
            ?.let { UserResponse.from(it) }
            ?: throw ModelNotFoundException("User", userId)
    }

}