package com.ianarbuckle.gymplannerservice.authentication.data.security

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.security.AuthenticatedUser
import com.ianarbuckle.gymplannerservice.security.SecurityUserLookup
import org.springframework.stereotype.Component

/**
 * Adapter that implements the security layer's [SecurityUserLookup] port using
 * the authentication domain's [UserRepository]. This keeps `:security` free of
 * any dependency on the concrete user model.
 */
@Component
class UserSecurityLookup(
    private val userRepository: UserRepository,
) : SecurityUserLookup {
    override suspend fun findByUsername(username: String): AuthenticatedUser? =
        userRepository.findByUsername(username)?.let { user ->
            AuthenticatedUserData(
                username = user.username,
                password = user.password,
                authorities = user.roles.map { it.name },
            )
        }
}

private data class AuthenticatedUserData(
    override val username: String,
    override val password: String?,
    override val authorities: Collection<String>,
) : AuthenticatedUser
