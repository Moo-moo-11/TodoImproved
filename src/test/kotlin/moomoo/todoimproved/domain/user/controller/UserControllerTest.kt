package moomoo.todoimproved.domain.user.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import moomoo.todoimproved.domain.exception.InvalidCredentialException
import moomoo.todoimproved.domain.exception.ModelNotFoundException
import moomoo.todoimproved.domain.exception.dto.ErrorResponse
import moomoo.todoimproved.domain.user.dto.CheckNicknameResponse
import moomoo.todoimproved.domain.user.dto.LoginResponse
import moomoo.todoimproved.domain.user.dto.UserResponse
import moomoo.todoimproved.domain.user.service.UserService
import moomoo.todoimproved.infra.security.jwt.JwtPlugin
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc, private val jwtPlugin: JwtPlugin,

    @MockkBean
    private val userService: UserService
) : DescribeSpec({
    extension(SpringExtension)

    afterContainer {
        clearAllMocks()
    }

    describe("POST /signup 은") {
        context("유효한 회원가입 요청을 보내면") {
            it("201 status code와 UserResponse를 반환해야한다.") {
                val nickname = "nickname"
                val password = "password"
                val confirmPassword = "password"
                val name = "name"
                val profileImageUrl = "string"

                every { userService.signUp(any()) } returns UserResponse(
                    id = 1L,
                    nickname = nickname,
                    name = name,
                    profileImageUrl = profileImageUrl
                )

                val requestBody =
                    """{"nickname":"$nickname","password":"$password","confirmPassword":"$confirmPassword","name":"$name","profileImageUrl":"$profileImageUrl"}"""

                val result = mockMvc.perform(
                    post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    UserResponse::class.java
                )

                result.response.status shouldBe 201

                responseDto.id shouldBe 1L
                responseDto.name shouldBe name
                responseDto.profileImageUrl shouldBe profileImageUrl
            }
        }

        context("중복된 닉네임으로 회원가입 요청을 보내면") {
            it("400 status code와 ErrorResponse를 반환해야한다.") {
                val nickname = "aa"
                val password = "password"
                val confirmPassword = "password"
                val name = "name"
                val profileImageUrl = "string"

                every { userService.signUp(any()) } throws IllegalArgumentException("중복된 닉네임입니다")

                val requestBody =
                    """{"nickname":"$nickname","password":"$password","confirmPassword":"$confirmPassword","name":"$name","profileImageUrl":"$profileImageUrl"}"""

                val result = mockMvc.perform(
                    post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    ErrorResponse::class.java
                )

                result.response.status shouldBe 400
                responseDto.message shouldBe "중복된 닉네임입니다"
                responseDto.errorCode shouldBe "400"
            }
        }
    }

    describe("GET /users 는") {
        context("존재하는 ID를 요청을 보낼 때") {
            it("200 status code와 '이미 존재하는 닉네임입니다' 메시지를 응답해야한다.") {
                val nickname = "nickname"

                every { userService.checkDuplicateNickname(any()) } returns CheckNicknameResponse(
                    message = "이미 존재하는 닉네임입니다"
                )

                val result = mockMvc.perform(
                    get("/users?nickname=$nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    CheckNicknameResponse::class.java
                )

                result.response.status shouldBe 200

                responseDto.message shouldBe "이미 존재하는 닉네임입니다"
            }
        }

        context("존재하지 않는 ID를 요청을 보낼 때") {
            it("200 status code와 '사용가능한 닉네임입니다' 메시지를 응답해야한다.") {
                val nickname = "nickname"

                every { userService.checkDuplicateNickname(any()) } returns CheckNicknameResponse(
                    message = "사용가능한 닉네임입니다"
                )

                val result = mockMvc.perform(
                    get("/users?nickname=$nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    CheckNicknameResponse::class.java
                )

                result.response.status shouldBe 200

                responseDto.message shouldBe "사용가능한 닉네임입니다"
            }
        }
    }

    describe("POST /login 은") {
        context("유효한 로그인 요청을 보내면") {
            it("200 status code와 토큰을 생성해 쿠키와 LoginResponse로 반환해야한다.") {
                val nickname = "nickname"
                val password = "password"

                val accessToken = jwtPlugin.generateAccessToken(1.toString(), nickname)

                val responseSlot = slot<HttpServletResponse>()

                every { userService.login(any(), capture(responseSlot)) } answers {
                    val response = responseSlot.captured
                    val cookie = Cookie("accessToken", accessToken)
                        .apply {
                            path = "/"
                            maxAge = 2 * 24 * 60 * 60
                            isHttpOnly = true
                        }

                    response.addCookie(cookie)

                    LoginResponse(
                        accessToken = accessToken
                    )
                }

                val requestBody = """{"nickname":"$nickname","password":"$password"}"""

                val result = mockMvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpect(status().isOk)
                    .andExpect(cookie().exists("accessToken"))
                    .andExpect(cookie().value("accessToken", accessToken))
                    .andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    LoginResponse::class.java
                )

                responseDto.accessToken shouldBe accessToken

            }
        }

        context("없는 닉네임으로 로그인 요청을 보내면") {
            it("400 status code와 ErrorResponse를 반환해야한다.") {
                val nickname = "nickname"
                val password = "password"

                val responseSlot = slot<HttpServletResponse>()

                every {
                    userService.login(
                        any(),
                        capture(responseSlot)
                    )
                } throws ModelNotFoundException("User", nickname)

                val requestBody = """{"nickname":"$nickname","password":"$password"}"""

                val result = mockMvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    ErrorResponse::class.java
                )

                result.response.status shouldBe 404
                responseDto.message shouldBe "User Not Found With Given Id: $nickname"
                responseDto.errorCode shouldBe "404"
            }
        }

        context("잘못된 비밀번호로 로그인 요청을 보내면") {
            it("400 status code와 ErrorResponse를 반환해야한다.") {
                val nickname = "nickname"
                val password = "password"

                val responseSlot = slot<HttpServletResponse>()

                every {
                    userService.login(
                        any(),
                        capture(responseSlot)
                    )
                } throws InvalidCredentialException("닉네임 또는 패스워드를 확인해주세요")

                val requestBody = """{"nickname":"$nickname","password":"$password"}"""

                val result = mockMvc.perform(
                    post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andReturn()

                val responseDto = jacksonObjectMapper().readValue(
                    result.response.getContentAsString(Charsets.UTF_8),
                    ErrorResponse::class.java
                )

                result.response.status shouldBe 400
                responseDto.message shouldBe "Invalid Credential: 닉네임 또는 패스워드를 확인해주세요"
                responseDto.errorCode shouldBe "400"
            }
        }
    }
})

