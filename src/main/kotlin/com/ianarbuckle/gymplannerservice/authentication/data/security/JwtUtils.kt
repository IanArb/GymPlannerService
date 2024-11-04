package com.ianarbuckle.gymplannerservice.authentication.data.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtUtils(
    @Value("\${gymplanner.app.jwtExpirationMs}")
    private val jwtExpirationMs: Long,
    @Value("\${gymplanner.app.jwtSecret}")
    private val jwtSecret: String
) {

    private val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun extractUsername(token: String?): String {
        return extractClaim(token) { claims -> claims.subject }
    }

    fun extractExpiration(token: String?): Date {
        return extractClaim(token) { claims -> claims.expiration }
    }

    fun <T> extractClaim(token: String?, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun generateToken(username: String): String {
        val claims: Map<String, Any?> = HashMap()
        return createToken(claims, username)
    }

    private fun createToken(claims: Map<String, Any?>, subject: String): String {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date.from(Instant.now().plus(jwtExpirationMs, ChronoUnit.MILLIS)))
            .signWith(key).compact()
    }

    fun validateToken(token: String?, username: String): Boolean {
        val extractedUsername = extractUsername(token)
        return (extractedUsername == username && !isTokenExpired(token))
    }
}