package com.ianarbuckle.gymplannerservice.authentication.data.security

import com.ianarbuckle.gymplannerservice.authentication.data.exception.TokenExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtils(
    @Value("\${gymplanner.app.jwtExpirationMs}") private val jwtExpirationMs: Long,
    @Value("\${gymplanner.app.jwtSecret}") private val jwtSecret: String,
) {
    private val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    private val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)

    fun extractUsername(token: String?): String = extractClaim(token) { claims -> claims.subject }

    fun extractExpiration(token: String?): Date =
        extractClaim(token) { claims -> claims.expiration }

    fun <T> extractClaim(
        token: String?,
        claimsResolver: (Claims) -> T,
    ): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String?): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload

    private fun isTokenExpired(token: String?): Boolean {
        val expirationDate = extractExpiration(token)
        if (expirationDate.before(Date())) {
            throw TokenExpiredException()
        }
        return false
    }

    fun generateToken(username: String): String {
        val claims: Map<String, Any?> = HashMap()
        return createToken(claims, username)
    }

    private fun createToken(
        claims: Map<String, Any?>,
        subject: String,
    ): String =
        Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS)))
            .signWith(key)
            .compact()

    fun validateToken(
        token: String?,
        username: String,
    ): Boolean =
        try {
            (extractUsername(token) == username && !isTokenExpired(token))
        } catch (e: TokenExpiredException) {
            logger.warn(
                "TokenExpiredException: Token has expired. Details: {}",
                e.message,
            )
            false
        } catch (e: Exception) {
            logger.error(
                "Exception during token validation. Details: {}",
                e.message,
                e,
            )
            false
        }
}
