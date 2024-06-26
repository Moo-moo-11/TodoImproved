package moomoo.todoimproved.domain.user.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import moomoo.todoimproved.domain.user.dto.*
import moomoo.todoimproved.domain.user.service.UserService
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.signUp(request))
    }

    @GetMapping("/users")
    fun checkDuplicateNickname(@RequestBody request: CheckNicknameRequest): ResponseEntity<CheckNicknameResponse> {
        return ResponseEntity
            .ok(userService.checkDuplicateNickname(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .ok(userService.login(request, response))
    }

    @GetMapping("/users/{userId}")
    fun getUser(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponse> {
        return ResponseEntity
            .ok(userService.getUser(userPrincipal, userId))
    }

    @PutMapping("/users/{userId}")
    fun updateUserProfileImage(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        @PathVariable userId: Long,
        @RequestBody request: UpdateUserProfileRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity
            .ok(userService.updateUser(userPrincipal, userId, request))
    }


}