package com.ianarbuckle.gymplannerservice.authentication.data.security

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
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

@Component
class JwtServerAuthenticationConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> =
        Mono
            .justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { it.startsWith("Bearer ") }
            .map { it.substring(7) }
            .map { BearerToken(it) }
}

@Component
class JWTAuthenticationManager(
    private val jwtUtil: JwtUtils,
    private val userRepository: UserRepository,
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
        val user = userRepository.findByUsername(username) ?: throw BadCredentialsException("No User found")

        val authorities = user.roles.map { SimpleGrantedAuthority(it.name.name) }

        if (jwtUtil.validateToken(token.value, user.username)) {
            return UsernamePasswordAuthenticationToken(user.username, user.password, authorities)
        }

        logger.error("Authentication error: Token is not valid")
        throw IllegalArgumentException("Token is not valid.")
    }
}
