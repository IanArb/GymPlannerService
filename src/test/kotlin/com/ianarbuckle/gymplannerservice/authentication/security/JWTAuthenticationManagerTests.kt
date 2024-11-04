package com.ianarbuckle.gymplannerservice.authentication.security

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.authentication.data.model.ERole
import com.ianarbuckle.gymplannerservice.authentication.data.model.Role
import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.authentication.data.security.BearerToken
import com.ianarbuckle.gymplannerservice.authentication.data.security.JWTAuthenticationManager
import com.ianarbuckle.gymplannerservice.authentication.data.security.JwtUtils
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.Test

class JWTAuthenticationManagerTests {
    private val jwtUtils: JwtUtils = mockk()
    private val userRepository: UserRepository = mockk()
    private val jwtAuthenticationManager: JWTAuthenticationManager = JWTAuthenticationManager(jwtUtils, userRepository)

    @Test
    fun `authenticate with valid token`() = runTest {
        val token = "validToken"
        val username = "testuser"
        val user = User(username = username, password = "password", roles = setOf(Role("1", ERole.ROLE_USER)), email = "test@mail.com")

        every { jwtUtils.extractUsername(token) } returns username
        coEvery { userRepository.findByUsername(username) } returns user
        every { jwtUtils.validateToken(token, username) } returns true

        val authentication = jwtAuthenticationManager.authenticate(BearerToken(token)).block()

        assertThat(authentication).isNotNull()
        assertThat(authentication?.name).isEqualTo(username)
        assertThat(authentication?.authorities?.size).isEqualTo(1)
        assertTrue(authentication?.authorities?.contains(SimpleGrantedAuthority(ERole.ROLE_USER.name)) == true)

        verify { jwtUtils.extractUsername(token) }
        coVerify { userRepository.findByUsername(username) }
        verify { jwtUtils.validateToken(token, username) }
    }

    @Test
    fun `authenticate with invalid token`() = runTest {
        val token = "invalidToken"

        every { jwtUtils.extractUsername(token) } throws BadCredentialsException("Invalid token")

        val exception = assertThrows<BadCredentialsException> {
            jwtAuthenticationManager.authenticate(BearerToken(token)).block()
        }

        assertThat(exception).isInstanceOf(BadCredentialsException::class.java)
        assertThat(exception.message).isEqualTo("Invalid token")

        verify { jwtUtils.extractUsername(token) }
    }

    @Test
    fun `authenticate with non-existent user`() = runTest {
        val token = "validToken"
        val username = "nonExistentUser"

        every { jwtUtils.extractUsername(token) } returns username
        coEvery { userRepository.findByUsername(username) } returns null

        val exception = assertThrows<BadCredentialsException> {
            jwtAuthenticationManager.authenticate(BearerToken(token)).block()
        }

        assertThat(exception).isInstanceOf(BadCredentialsException::class.java)

        verify { jwtUtils.extractUsername(token) }
        coVerify { userRepository.findByUsername(username) }
    }

    @Test
    fun `authenticate with invalid token validation`() = runTest {
        val token = "validToken"
        val username = "testuser"
        val user = User(username = username, password = "password", roles = setOf(Role("1", ERole.ROLE_USER)), email = "test@mail.com")

        every { jwtUtils.extractUsername(token) } returns username
        coEvery { userRepository.findByUsername(username) } returns user
        every { jwtUtils.validateToken(token, username) } returns false

        val exception = assertThrows<BadCredentialsException> {
            jwtAuthenticationManager.authenticate(BearerToken(token)).block()
        }

        assertThat(exception).isInstanceOf(BadCredentialsException::class.java)

        verify { jwtUtils.extractUsername(token) }
        coVerify { userRepository.findByUsername(username) }
        verify { jwtUtils.validateToken(token, username) }
    }
}