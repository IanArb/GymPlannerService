package com.ianarbuckle.gymplannerservice.security

import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BearerToken(
    val value: String,
) : AbstractAuthenticationToken(AuthorityUtils.NO_AUTHORITIES) {
    override fun getCredentials(): Any = value

    override fun getPrincipal(): Any = value
}

private const val SUBSTRING_LENGTH = 7

@Component
class JwtServerAuthenticationConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val header = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return Mono
            .justOrEmpty(header)
            .filter { it.startsWith("Bearer ") }
            .map { it.substring(SUBSTRING_LENGTH) }
            .map { BearerToken(it) }
    }
}

@Component
class JWTAuthenticationManager(
    private val jwtUtil: JwtUtils,
    private val userLookup: SecurityUserLookup,
) : ReactiveAuthenticationManager {
    private val logger: Logger = LoggerFactory.getLogger(JWTAuthenticationManager::class.java)

    override fun authenticate(authentication: Authentication): Mono<Authentication> =
        Mono
            .justOrEmpty(authentication)
            .filter { auth -> auth is BearerToken }
            .cast(BearerToken::class.java)
            .flatMap { jwt -> mono { validate(jwt) } }
            .onErrorMap { error ->
                logger.error("Authentication error: ${error.message}", error)
                BadCredentialsException("Invalid token")
            }

    private suspend fun validate(token: BearerToken): Authentication {
        val username = jwtUtil.extractUsername(token.value)
        val user =
            userLookup.findByUsername(username)
                ?: throw BadCredentialsException("No User found")

        val authorities = user.authorities.map { SimpleGrantedAuthority(it) }

        if (jwtUtil.validateToken(token.value, user.username)) {
            logger.info(
                "Authenticated user='{}' authorities={}",
                user.username,
                authorities,
            )
            return UsernamePasswordAuthenticationToken(user.username, user.password, authorities)
        }

        throw IllegalArgumentException("Token is not valid.")
    }
}
