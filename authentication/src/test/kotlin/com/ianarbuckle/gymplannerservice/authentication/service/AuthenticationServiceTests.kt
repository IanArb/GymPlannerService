package com.ianarbuckle.gymplannerservice.authentication.service

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.authentication.ERole
import com.ianarbuckle.gymplannerservice.authentication.User
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationService
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationServiceImpl
import com.ianarbuckle.gymplannerservice.authentication.data.service.UserProfileRegistrar
import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.security.JwtUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.sql.Date
import kotlin.test.Test

class AuthenticationServiceTests {
    private val userRepository: UserRepository = mockk()
    private val userProfileRegistrar: UserProfileRegistrar = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val jwtUtils: JwtUtils = mockk()

    private val authenticationService: AuthenticationService =
        AuthenticationServiceImpl(
            userRepository,
            userProfileRegistrar,
            passwordEncoder,
            jwtUtils,
        )

    private fun testUser(
        username: String = "testuser",
        password: String = "encodedPassword",
    ): User =
        User(
            id = "123456",
            username = username,
            password = password,
            email = "user@mail.com",
            roles = setOf(ERole.ROLE_USER),
            pushNotificationToken = "pushToken",
        )

    @Test
    fun `test authenticationUser with valid credentials`() =
        runTest {
            val username = "testuser"
            val password = "password"
            val encodedPassword = "encodedPassword"
            val jwtToken = "jwtToken"
            val expiration: Long = 1000

            coEvery { userRepository.findByUsername(username) } returns testUser()
            every { passwordEncoder.matches(password, encodedPassword) } returns true
            every { jwtUtils.generateToken(username) } returns jwtToken
            every { jwtUtils.extractExpiration(jwtToken) } returns Date(expiration)

            val result = authenticationService.authenticationUser(LoginRequest(username, password))

            assertThat(jwtToken).isEqualTo(result.token)
            assertThat(expiration).isEqualTo(result.expiration)

            coVerify { userRepository.findByUsername(username) }
            verify { passwordEncoder.matches(password, encodedPassword) }
            verify { jwtUtils.generateToken(username) }
            verify { jwtUtils.extractExpiration(jwtToken) }
        }

    @Test
    fun `test createUser with valid data`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "newuser",
                    email = "newuser@mail.com",
                    password = "password",
                    roles = setOf("user"),
                    firstName = "New",
                    surname = "User",
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
            coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
            coEvery { userRepository.save(any()) } returnsArgument 0
            coEvery { userProfileRegistrar.save(any()) } returns Unit
            every { passwordEncoder.encode(signUpRequest.password) } returns "encodedPassword"

            val result = authenticationService.createUser(signUpRequest)

            assertThat("User registered successfully!").isEqualTo(result.message)

            coVerify { userRepository.existsByUsername(signUpRequest.username) }
            coVerify { userRepository.existsByEmail(signUpRequest.email) }
            coVerify { userRepository.save(match { it.roles == setOf(ERole.ROLE_USER) }) }
            coVerify { userProfileRegistrar.save(any()) }
            verify { passwordEncoder.encode(signUpRequest.password) }
        }

    @Test
    fun `test createUser propagates gymLocation to UserProfile`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "newuser",
                    email = "newuser@mail.com",
                    password = "password",
                    roles = setOf("user"),
                    firstName = "New",
                    surname = "User",
                    gymLocation = GymLocation.CLONTARF,
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
            coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
            coEvery { userRepository.save(any()) } returnsArgument 0
            coEvery { userProfileRegistrar.save(any()) } returns Unit
            every { passwordEncoder.encode(signUpRequest.password) } returns "encodedPassword"

            authenticationService.createUser(signUpRequest)

            coVerify { userProfileRegistrar.save(match { it.gymLocation == GymLocation.CLONTARF }) }
        }

    @Test
    fun `test createUser maps admin role string to ERole_ROLE_ADMIN`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "adminuser",
                    email = "admin@mail.com",
                    password = "password",
                    roles = setOf("admin"),
                    firstName = "Admin",
                    surname = "User",
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
            coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
            coEvery { userRepository.save(any()) } returnsArgument 0
            coEvery { userProfileRegistrar.save(any()) } returns Unit
            every { passwordEncoder.encode(signUpRequest.password) } returns "encodedPassword"

            authenticationService.createUser(signUpRequest)

            coVerify { userRepository.save(match { it.roles == setOf(ERole.ROLE_ADMIN) }) }
        }

    @Test
    fun `test createUser with existing username`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "existinguser",
                    email = "newuser@mail.com",
                    password = "password",
                    roles = setOf("user"),
                    firstName = "New",
                    surname = "User",
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns true

            val exception =
                assertThrows<UserAlreadyExistsException> {
                    authenticationService.createUser(signUpRequest)
                }

            assertThat(exception).isInstanceOf(UserAlreadyExistsException::class.java)

            coVerify { userRepository.existsByUsername(signUpRequest.username) }
        }

    @Test
    fun `test createUser with existing email`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "newuser",
                    email = "existing@mail.com",
                    password = "password",
                    roles = setOf("user"),
                    firstName = "New",
                    surname = "User",
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
            coEvery { userRepository.existsByEmail(signUpRequest.email) } returns true

            val exception =
                assertThrows<EmailAlreadyExistsException> {
                    authenticationService.createUser(signUpRequest)
                }

            assertThat(exception).isInstanceOf(EmailAlreadyExistsException::class.java)

            coVerify { userRepository.existsByUsername(signUpRequest.username) }
            coVerify { userRepository.existsByEmail(signUpRequest.email) }
        }

    @Test
    fun `test createUser with unknown role string defaults to ROLE_USER`() =
        runTest {
            val signUpRequest =
                SignUpRequest(
                    username = "newuser",
                    email = "newuser@mail.com",
                    password = "password",
                    roles = setOf("invalidrole"),
                    firstName = "New",
                    surname = "User",
                )

            coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
            coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
            coEvery { userRepository.save(any()) } returnsArgument 0
            coEvery { userProfileRegistrar.save(any()) } returns Unit
            every { passwordEncoder.encode(signUpRequest.password) } returns "encodedPassword"

            authenticationService.createUser(signUpRequest)

            coVerify { userRepository.save(match { it.roles == setOf(ERole.ROLE_USER) }) }
        }
}
