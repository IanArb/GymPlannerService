package com.ianarbuckle.gymplannerservice.security

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.Test

class JWTAuthenticationManagerTests {
    private val jwtUtils: JwtUtils = mockk()
    private val userLookup: SecurityUserLookup = mockk()
    private val jwtAuthenticationManager: JWTAuthenticationManager =
        JWTAuthenticationManager(jwtUtils, userLookup)

    private fun authenticatedUser(
        name: String,
        vararg authorities: String,
    ): AuthenticatedUser =
        object : AuthenticatedUser {
            override val username = name
            override val password = "password"
            override val authorities = authorities.toList()
        }

    @Test
    fun `authenticate with valid token`() =
        runTest {
            val token = "validToken"
            val username = "testuser"
            val user = authenticatedUser(username, "ROLE_USER")

            every { jwtUtils.extractUsername(token) } returns username
            coEvery { userLookup.findByUsername(username) } returns user
            every { jwtUtils.validateToken(token, username) } returns true

            val authentication = jwtAuthenticationManager.authenticate(BearerToken(token)).block()

            assertThat(authentication).isNotNull()
            assertThat(authentication?.name).isEqualTo(username)
            assertThat(authentication?.authorities?.size).isEqualTo(1)
            assertTrue(
                authentication?.authorities?.contains(SimpleGrantedAuthority("ROLE_USER")) ==
                    true,
            )

            verify { jwtUtils.extractUsername(token) }
            coVerify { userLookup.findByUsername(username) }
            verify { jwtUtils.validateToken(token, username) }
        }

    @Test
    fun `authenticate with invalid token`() =
        runTest {
            val token = "invalidToken"

            every { jwtUtils.extractUsername(token) } throws BadCredentialsException("Invalid token")

            val exception =
                assertThrows<BadCredentialsException> {
                    jwtAuthenticationManager.authenticate(BearerToken(token)).block()
                }

            assertThat(exception).isInstanceOf(BadCredentialsException::class.java)
            assertThat(exception.message).isEqualTo("Invalid token")

            verify { jwtUtils.extractUsername(token) }
        }

    @Test
    fun `authenticate with non-existent user`() =
        runTest {
            val token = "validToken"
            val username = "nonExistentUser"

            every { jwtUtils.extractUsername(token) } returns username
            coEvery { userLookup.findByUsername(username) } returns null

            val exception =
                assertThrows<BadCredentialsException> {
                    jwtAuthenticationManager.authenticate(BearerToken(token)).block()
                }

            assertThat(exception).isInstanceOf(BadCredentialsException::class.java)

            verify { jwtUtils.extractUsername(token) }
            coVerify { userLookup.findByUsername(username) }
        }

    @Test
    fun `authenticate with invalid token validation`() =
        runTest {
            val token = "validToken"
            val username = "testuser"
            val user = authenticatedUser(username, "ROLE_USER")

            every { jwtUtils.extractUsername(token) } returns username
            coEvery { userLookup.findByUsername(username) } returns user
            every { jwtUtils.validateToken(token, username) } returns false

            val exception =
                assertThrows<BadCredentialsException> {
                    jwtAuthenticationManager.authenticate(BearerToken(token)).block()
                }

            assertThat(exception).isInstanceOf(BadCredentialsException::class.java)

            verify { jwtUtils.extractUsername(token) }
            coVerify { userLookup.findByUsername(username) }
            verify { jwtUtils.validateToken(token, username) }
        }
}
