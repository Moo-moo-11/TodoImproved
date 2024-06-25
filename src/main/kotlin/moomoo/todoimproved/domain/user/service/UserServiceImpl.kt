package moomoo.todoimproved.domain.user.service

import moomoo.todoimproved.domain.user.dto.*
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {
    override fun signUp(request: SignUpRequest): UserResponse {
        TODO("Not yet implemented")
    }

    override fun login(request: LoginRequest): LoginResponse {
        TODO("Not yet implemented")
    }

    override fun getUser(userId: Long): UserResponse {
        TODO("Not yet implemented")
    }

    override fun updateUser(userId: Long, request: UpdateUserProfileRequest): UserResponse {
        TODO("Not yet implemented")
    }
}