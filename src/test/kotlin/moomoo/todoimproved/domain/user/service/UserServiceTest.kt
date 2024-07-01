package moomoo.todoimproved.domain.user.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletResponse
import moomoo.todoimproved.domain.exception.InvalidCredentialException
import moomoo.todoimproved.domain.user.dto.LoginRequest
import moomoo.todoimproved.domain.user.dto.SignUpRequest
import moomoo.todoimproved.domain.user.model.User
import moomoo.todoimproved.domain.user.repository.UserRepository
import moomoo.todoimproved.infra.security.jwt.JwtPlugin
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest : BehaviorSpec({

    val userRepository: UserRepository = mockk()
    val passwordEncoder: PasswordEncoder = mockk()
    val jwtPlugin: JwtPlugin = mockk()
    val userService = UserServiceImpl(userRepository, passwordEncoder, jwtPlugin)

    afterContainer {
        clearAllMocks()
    }

    given("유효한 SignUpRequest를 가지고") {
        `when`("signUp() 실행하면") {
            then("UserResponse가 반환된다.") {
                val req = SignUpRequest(
                    nickname = "nickname",
                    password = "password",
                    confirmPassword = "password",
                    name = "name",
                    profileImageUrl = "profileImageUrl"
                )
                val userId = 1L
                val user = User(
                    nickname = "nickname",
                    password = "password",
                    name = "name",
                    profileImageUrl = "profileImageUrl"
                ).apply { this.id = userId }

                every { userRepository.existsByNickname(any()) } returns false
                every { req.toEntity(passwordEncoder) } returns user
                every { passwordEncoder.encode(req.password) } returns "encodedPassword"
                every { userRepository.save(any()) } returns user

                val response = userService.signUp(req)

                response.nickname shouldBe req.nickname
                verify { userRepository.existsByNickname(req.nickname) }
                verify { req.toEntity(passwordEncoder) }
                verify { userRepository.save(any()) }
            }
        }
    }

    given("중복된 닉네임이 담긴 SignUpRequest를 가지고") {
        `when`("signUp() 실행하면") {
            then("IllegalArgumentException이 발생한다.") {
                val req = SignUpRequest(
                    nickname = "nickname",
                    password = "password",
                    confirmPassword = "password",
                    name = "name",
                    profileImageUrl = "profileImageUrl"
                )

                every { userRepository.existsByNickname(any()) } returns true


                val exception = shouldThrow<IllegalArgumentException> {
                    userService.signUp(req)
                }

                exception.message shouldBe "중복된 닉네임입니다"
                verify { userRepository.existsByNickname(req.nickname) }
            }
        }
    }

    given("유효한 닉네임을 가지고") {
        `when`("checkDuplicateNickname() 실행하면") {
            then("'사용가능한 닉네임입니다' 라는 메시지가 담긴 CheckNicknameResponse가 반환된다.") {
                val nickname = "nickname"

                every { userRepository.existsByNickname(any()) } returns false

                val response = userService.checkDuplicateNickname(nickname)

                response.message shouldBe "사용가능한 닉네임입니다"
                verify { userRepository.existsByNickname(nickname) }
            }
        }
    }

    given("중복된 닉네임을 가지고") {
        `when`("checkDuplicateNickname() 실행하면") {
            then("'사용가능한 닉네임입니다' 라는 메시지가 담긴 CheckNicknameResponse가 반환된다.") {
                val nickname = "nickname"

                every { userRepository.existsByNickname(any()) } returns true

                val response = userService.checkDuplicateNickname(nickname)

                response.message shouldBe "이미 존재하는 닉네임입니다"
                verify { userRepository.existsByNickname(nickname) }
            }
        }
    }

    given("유효한 LoginRequest를 가지고") {
        `when`("login() 실행하면") {
            then("토큰이 쿠키와 LoginResponse로 반환된다.") {
                val req = LoginRequest(
                    nickname = "nickname",
                    password = "password"
                )
                val res = mockk<HttpServletResponse>(relaxed = true)
                val userId = 1L
                val user = User(
                    nickname = "nickname",
                    password = "password",
                    name = "name",
                    profileImageUrl = "profileImageUrl"
                ).apply { this.id = userId }

                val accessToken = "mockedAccessToken"

                every { userRepository.findByNickname(any()) } returns user

                every { passwordEncoder.matches(any(), any()) } returns true

                every { jwtPlugin.generateAccessToken(any(), any()) } returns accessToken

                val response = userService.login(req, res)

                response.accessToken shouldBe "mockedAccessToken"
                verify {
                    res.addCookie(withArg {
                        it.name shouldBe "accessToken"
                        it.value shouldBe "mockedAccessToken"
                        it.path shouldBe "/"
                        it.maxAge shouldBe 2 * 24 * 60 * 60
                        it.isHttpOnly shouldBe true
                    })
                }
            }
        }
    }

    given("틀린 비밀번호가 담긴 LoginRequest를 가지고") {
        `when`("login() 실행하면") {
            then("'InvalidCredentialException이 발생한다.") {
                val req = LoginRequest(
                    nickname = "nickname",
                    password = "password"
                )
                val res = mockk<HttpServletResponse>(relaxed = true)
                val userId = 1L
                val user = User(
                    nickname = "nickname",
                    password = "password",
                    name = "name",
                    profileImageUrl = "profileImageUrl"
                ).apply { this.id = userId }

                every { userRepository.findByNickname(any()) } returns user

                every { passwordEncoder.matches(any(), any()) } returns false

                val exception = shouldThrow<InvalidCredentialException> {
                    userService.login(req, res)
                }

                exception.message shouldBe "Invalid Credential: 닉네임 또는 패스워드를 확인해주세요"
                verify { userRepository.findByNickname(req.nickname) }
                verify { passwordEncoder.matches(req.password, user.password) }
            }
        }
    }
})