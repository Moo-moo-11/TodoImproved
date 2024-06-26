package moomoo.todoimproved.infra.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal(
    val id: Long,
    val userIdentifier: String,
    val authorities: Collection<GrantedAuthority>
) {
    constructor(id: Long, userIdentifier: String, roles: Set<String>) : this(
        id = id, userIdentifier = userIdentifier, authorities = roles.map { SimpleGrantedAuthority("ROLE_${it}") }
    )
}