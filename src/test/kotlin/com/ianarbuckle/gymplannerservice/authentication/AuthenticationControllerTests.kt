package com.ianarbuckle.gymplannerservice.authentication

import com.ianarbuckle.gymplannerservice.authentication.data.domain.JwtResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.MessageResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.RoleNotFoundException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationService
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.mockito.Mockito.`when` as whenever
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [AuthenticationController::class], excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class AuthenticationControllerTests {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var authenticationService: AuthenticationService

    @Test
    fun `test login with valid credentials`() = runTest {
        val loginRequest = LoginRequest(username = "testuser", password = "password")
        val loginRequestJson = """
            {
                "username": "${loginRequest.username}",
                "password": "${loginRequest.password}"
            }
        """.trimIndent()

        whenever(authenticationService.authenticationUser(loginRequest)).thenReturn(JwtResponse(
            token = "validToken"
        ))

        webTestClient.post().uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(loginRequestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isNotEmpty
    }

    @Test
    fun `test login with invalid credentials`() = runTest {
        val loginRequest = LoginRequest(username = "testuser", password = "wrongpassword")
        val loginRequestJson = """
            {
                "username": "${loginRequest.username}",
                "password": "${loginRequest.password}"
            }
        """.trimIndent()

        whenever(authenticationService.authenticationUser(loginRequest)).thenThrow(BadCredentialsException("Invalid username or password"))

        webTestClient.post().uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(loginRequestJson))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `test login with invalid user`() = runTest {
        val loginRequest = LoginRequest(username = "testuser", password = "wrongpassword")
        val loginRequestJson = """
            {
                "username": "${loginRequest.username}",
                "password": "${loginRequest.password}"
            }
        """.trimIndent()

        whenever(authenticationService.authenticationUser(loginRequest)).thenThrow(UserNotFoundException())

        webTestClient.post().uri("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(loginRequestJson))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `test register with valid data`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "newuser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )
        val signUpRequestJson = """
            {
                "username": "${signUpRequest.username}",
                "email": "${signUpRequest.email}",
                "password": "${signUpRequest.password}",
                "roles": ["user"],
                "firstName": "${signUpRequest.firstName}",
                "surname": "${signUpRequest.surname}"
            }
        """.trimIndent()

        whenever(authenticationService.createUser(signUpRequest)).thenReturn(MessageResponse(
            message = "User registered successfully!"
        ))

        webTestClient.post().uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(signUpRequestJson))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.message").isEqualTo("User registered successfully!")
    }

    @Test
    fun `test register with existing username`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "existinguser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )
        val signUpRequestJson = """
            {
                "username": "${signUpRequest.username}",
                "email": "${signUpRequest.email}",
                "password": "${signUpRequest.password}",
                "roles": ["user"],
                "firstName": "${signUpRequest.firstName}",
                "surname": "${signUpRequest.surname}"
            }
        """.trimIndent()

        whenever(authenticationService.createUser(signUpRequest)).thenThrow(UserAlreadyExistsException())

        webTestClient.post().uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(signUpRequestJson))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
    }

    @Test
    fun `test register with existing email`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "existinguser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("user"),
            firstName = "New",
            surname = "User"
        )
        val signUpRequestJson = """
            {
                "username": "${signUpRequest.username}",
                "email": "${signUpRequest.email}",
                "password": "${signUpRequest.password}",
                "roles": ["user"],
                "firstName": "${signUpRequest.firstName}",
                "surname": "${signUpRequest.surname}"
            }
        """.trimIndent()

        whenever(authenticationService.createUser(signUpRequest)).thenThrow(EmailAlreadyExistsException())

        webTestClient.post().uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(signUpRequestJson))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
    }

    @Test
    fun `test register with invalid role`() = runTest {
        val signUpRequest = SignUpRequest(
            username = "existinguser",
            email = "newuser@mail.com",
            password = "password",
            roles = setOf("invalid"),
            firstName = "New",
            surname = "User"
        )
        val signUpRequestJson = """
            {
                "username": "${signUpRequest.username}",
                "email": "${signUpRequest.email}",
                "password": "${signUpRequest.password}",
                "roles": ["invalid"],
                "firstName": "${signUpRequest.firstName}",
                "surname": "${signUpRequest.surname}"
            }
        """.trimIndent()

        whenever(authenticationService.createUser(signUpRequest)).thenThrow(RoleNotFoundException())

        webTestClient.post().uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(signUpRequestJson))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
    }
}