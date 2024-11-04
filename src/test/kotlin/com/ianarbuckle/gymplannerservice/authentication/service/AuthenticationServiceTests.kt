package com.ianarbuckle.gymplannerservice.authentication.service

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.RoleNotFoundException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.model.ERole
import com.ianarbuckle.gymplannerservice.authentication.data.model.Role
import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import com.ianarbuckle.gymplannerservice.authentication.data.repository.RoleRepository
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserAccountRepository
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.authentication.data.security.JwtUtils
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationService
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationServiceImpl
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.Test

class AuthenticationServiceTests {
    private val userRepository: UserRepository = mockk()
    private val userAccountRepository: UserAccountRepository = mockk()
    private val roleRepository: RoleRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()
    private val jwtUtils: JwtUtils = mockk()

    private val authenticationService: AuthenticationService = AuthenticationServiceImpl(
        userRepository,
        userAccountRepository,
        roleRepository,
        passwordEncoder,
        jwtUtils,
    )

    @Test
    fun `test authenticationUser with valid credentials`() = runTest {
        val username = "testuser"
        val password = "password"
        val encodedPassword = "encodedPassword"
        val jwtToken = "jwtToken"

        val user = User(
            username = username,
            password = encodedPassword,
            roles = setOf(Role(
                id = "1",
                ERole.ROLE_USER
            )),
            email = "ian@mail.com")

        coEvery { userRepository.findByUsername(username) } returns user
        every { passwordEncoder.matches(password, encodedPassword) } returns true
        every { jwtUtils.generateToken(username) } returns jwtToken

        val result = authenticationService.authenticationUser(LoginRequest(username, password))

        assertThat(jwtToken).isEqualTo(result.token)

        coVerify { userRepository.findByUsername(username) }
        verify { passwordEncoder.matches(password, encodedPassword) }
        verify { jwtUtils.generateToken(username) }
    }


    @Test
    fun `test createUser with valid data`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "newuser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )

        coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
        coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
        coEvery { roleRepository.findByName(ERole.ROLE_USER) } returns Role("1", ERole.ROLE_USER)
        coEvery { userRepository.save(any()) } returnsArgument 0
        coEvery { userAccountRepository.save(any()) } returnsArgument 0
        every { passwordEncoder.encode(signUpRequest.password) } returns "encodedPassword"

        val result = authenticationService.createUser(signUpRequest)

        assertThat("User registered successfully!").isEqualTo(result.message)

        coVerify { userRepository.existsByUsername(signUpRequest.username) }
        coVerify { userRepository.existsByEmail(signUpRequest.email) }
        coVerify { roleRepository.findByName(ERole.ROLE_USER) }
        coVerify { userRepository.save(any()) }
        coVerify { userAccountRepository.save(any()) }
        verify { passwordEncoder.encode(signUpRequest.password) }
    }

    @Test
    fun `test createUser with existing username`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "existinguser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )

        coEvery { userRepository.existsByUsername(signUpRequest.username) } returns true

        val exception = assertThrows<UserAlreadyExistsException> {
            authenticationService.createUser(signUpRequest)
        }

        assertThat(exception).isInstanceOf(UserAlreadyExistsException::class.java)

        coVerify { userRepository.existsByUsername(signUpRequest.username) }
    }

    @Test
    fun `test createUser with existing email`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "newuser",
            email = "existing@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )

        coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
        coEvery { userRepository.existsByEmail(signUpRequest.email) } returns true

        val exception = assertThrows<EmailAlreadyExistsException> {
            authenticationService.createUser(signUpRequest)
        }

        assertThat(exception).isInstanceOf(EmailAlreadyExistsException::class.java)

        coVerify { userRepository.existsByUsername(signUpRequest.username) }
        coVerify { userRepository.existsByEmail(signUpRequest.email) }
    }

    @Test
    fun `test createUser with invalid role`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "newuser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("invalidrole"),
            firstName = "New",
            surname = "User"
        )

        coEvery { userRepository.existsByUsername(signUpRequest.username) } returns false
        coEvery { userRepository.existsByEmail(signUpRequest.email) } returns false
        coEvery { roleRepository.findByName(any()) } returns null

        val exception = assertThrows<RoleNotFoundException> {
            authenticationService.createUser(signUpRequest)
        }

        assertThat(exception).isInstanceOf(RoleNotFoundException::class.java)

        coVerify { userRepository.existsByUsername(signUpRequest.username) }
        coVerify { userRepository.existsByEmail(signUpRequest.email) }
        coVerify { roleRepository.findByName(any()) }
    }
}