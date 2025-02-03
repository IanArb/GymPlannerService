package com.ianarbuckle.gymplannerservice.authentication.security

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.authentication.data.security.JwtUtils
import java.util.Date
import kotlin.test.Test

class JwtUtilsTests {
    private val jwtSecret = "mysecretkeymysecretkeymysecretkeymysecretkey"
    private val jwtExpirationMs: Long = 3600000 // 1 hour

    private val jwtUtils: JwtUtils = JwtUtils(jwtExpirationMs, jwtSecret)

    @Test
    fun `generateToken should create a valid token`() {
        val username = "testuser"
        val token = jwtUtils.generateToken(username)
        assertThat(token).isNotNull()
    }

    @Test
    fun `extractUsername should return the correct username`() {
        val username = "testuser"
        val token = jwtUtils.generateToken(username)
        val extractedUsername = jwtUtils.extractUsername(token)
        assertThat(extractedUsername).isEqualTo(username)
    }

    @Test
    fun `extractExpiration should return the correct expiration date`() {
        val username = "testuser"
        val token = jwtUtils.generateToken(username)
        val expiration = jwtUtils.extractExpiration(token)
        assertThat(expiration.after(Date())).isTrue()
    }

    @Test
    fun `validateToken should return true for a valid token`() {
        val username = "testuser"
        val token = jwtUtils.generateToken(username)
        val isValid = jwtUtils.validateToken(token, username)
        assertThat(isValid).isTrue()
    }

    @Test
    fun `validateToken should return false for an invalid token`() {
        val username = "testuser"
        val token = jwtUtils.generateToken(username)
        val isValid = jwtUtils.validateToken(token, "wronguser")
        assertThat(isValid).isFalse()
    }

    @Test
    fun `isTokenExpired should return false for an expired token`() {
        val expiredJwtUtils = JwtUtils(0, jwtSecret) // Token expires immediately
        val username = "testuser"
        val token = expiredJwtUtils.generateToken(username)
        val isExpired = expiredJwtUtils.validateToken(token, username)
        assertThat(isExpired).isFalse()
    }
}
