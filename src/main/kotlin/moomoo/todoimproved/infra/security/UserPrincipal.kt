package moomoo.todoimproved.infra.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal(
    val id: Long,
    val userNickname: String,
    val authorities: Collection<GrantedAuthority>
) {
    constructor(id: Long, userNickname: String, roles: Set<String>) : this(
        id = id, userNickname = userNickname, authorities = roles.map { SimpleGrantedAuthority("ROLE_${it}") }
    )
}