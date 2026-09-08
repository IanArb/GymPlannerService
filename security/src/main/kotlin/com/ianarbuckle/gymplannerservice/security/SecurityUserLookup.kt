package com.ianarbuckle.gymplannerservice.security

/**
 * The minimal view of a user that the security layer needs to authenticate a
 * request. Authorities are the granted-authority names (e.g. "ROLE_ADMIN").
 */
interface AuthenticatedUser {
    val username: String
    val password: String?
    val authorities: Collection<String>
}

/**
 * Port for looking up a user by username. Implemented by the authentication
 * domain so that `:security` does not depend on the concrete user model or
 * repository (dependency inversion).
 */
interface SecurityUserLookup {
    suspend fun findByUsername(username: String): AuthenticatedUser?
}
