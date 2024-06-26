package moomoo.todoimproved.infra.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import moomoo.todoimproved.infra.security.UserPrincipal
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtPlugin: JwtPlugin
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val jwt = request.getBearerToken()

        if (jwt is String) {
            jwtPlugin.validateToken(jwt)
                .onSuccess {
                    val userId = it.payload.subject.toLong()
                    val userNickname = it.payload["userNickname"] as String

                    val principal = UserPrincipal(id = userId, userNickname = userNickname, roles = setOf())

                    val authentication =
                        JwtAuthenticationToken(principal, WebAuthenticationDetailsSource().buildDetails(request))

                    SecurityContextHolder.getContext().authentication = authentication
                }
                .onFailure { exception ->
                    when (exception) {
                        is ExpiredJwtException -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired")
                        }

                        else -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                        }
                    }
                }
        }

        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.getBearerToken(): String? {
        val headerValue = this.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (headerValue.contains("Bearer ")) headerValue.removePrefix("Bearer ") else null
    }
}