package moomoo.todoimproved.domain.user.service

import jakarta.servlet.http.HttpServletResponse
import moomoo.todoimproved.domain.user.dto.*
import moomoo.todoimproved.infra.security.UserPrincipal


interface UserService {
    fun signUp(request: SignUpRequest): UserResponse
    fun login(request: LoginRequest, response: HttpServletResponse): LoginResponse
    fun getUser(userPrincipal: UserPrincipal, userId: Long): UserResponse
    fun updateUser(userPrincipal: UserPrincipal, userId: Long, request: UpdateUserProfileRequest): UserResponse
    fun checkDuplicateNickname(nickname: String): CheckNicknameResponse
}