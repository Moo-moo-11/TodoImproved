package moomoo.todoimproved.domain.user.controller

import moomoo.todoimproved.domain.user.dto.*
import moomoo.todoimproved.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.signUp(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .ok(userService.login(request))
    }

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponse> {
        return ResponseEntity
            .ok(userService.getUser(userId))
    }

    @PutMapping("/users/{userId}")
    fun updateUserProfileImage(
        @PathVariable userId: Long,
        @RequestBody request: UpdateUserProfileRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity
            .ok(userService.updateUser(userId, request))
    }
}