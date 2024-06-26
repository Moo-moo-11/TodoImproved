package moomoo.todoimproved.infra.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtPlugin(
    private val jwtProperties: JwtProperties
) {
    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(Charsets.UTF_8))

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)
        }
    }

    fun generateAccessToken(subject: String, userNickname: String): String {
        return generateToken(subject, userNickname, Duration.ofHours(jwtProperties.accessTokenExpirationHour))
    }

    fun generateToken(subject: String, userNickname: String, expirationHour: Duration): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(subject)
            .claims(mapOf("userNickname" to userNickname))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationHour)))
            .signWith(key)
            .compact()
    }
}