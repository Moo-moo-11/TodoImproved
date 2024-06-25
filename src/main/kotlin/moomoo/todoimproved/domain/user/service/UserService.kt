package moomoo.todoimproved.domain.user.service

import moomoo.todoimproved.domain.user.dto.*

interface UserService {
    fun signUp(request: SignUpRequest): UserResponse
    fun login(request: LoginRequest): LoginResponse
    fun getUser(userId: Long): UserResponse
    fun updateUser(userId: Long, request: UpdateUserProfileRequest): UserResponse
}