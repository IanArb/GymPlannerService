package com.ianarbuckle.gymplannerservice.authentication.security

import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.authentication.AuthenticationController
import com.ianarbuckle.gymplannerservice.authentication.data.domain.MessageResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.security.JWTAuthenticationManager
import com.ianarbuckle.gymplannerservice.authentication.data.security.JwtServerAuthenticationConverter
import com.ianarbuckle.gymplannerservice.authentication.data.security.SecurityConfig
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationService
import com.ianarbuckle.gymplannerservice.facilityStatus.FacilityStatusController
import com.ianarbuckle.gymplannerservice.facilityStatus.FacilityStatusService
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [AuthenticationController::class, FacilityStatusController::class],
)
@Import(SecurityConfig::class, JwtServerAuthenticationConverter::class)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class SecurityConfigTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var authenticationService: AuthenticationService

    @Suppress("UnusedPrivateProperty")
    @MockitoBean
    private lateinit var facilityStatusService: FacilityStatusService

    @MockitoBean private lateinit var jwtAuthenticationManager: JWTAuthenticationManager

    @Test
    fun `passwordEncoder should be BCryptPasswordEncoder`() {
        val securityConfig = SecurityConfig(jwtAuthenticationManager, "http://localhost:8080")
        assertThat(securityConfig.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder::class.java)
    }

    @Test
    fun `auth endpoints should be accessible without token`() = runTest {
        val signUpRequest =
            SignUpRequest(
                username = "newuser",
                firstName = "New",
                surname = "User",
                email = "newuser@mail.com",
                password = "password123",
                roles = setOf("user"),
            )
        `when`(authenticationService.createUser(signUpRequest))
            .thenReturn(MessageResponse(message = "User registered successfully!"))

        webTestClient
            .post()
            .uri("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signUpRequest)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `protected endpoints should return 401 without token`() = runTest {
        webTestClient.get().uri("/api/v1/facilities").exchange().expectStatus().isUnauthorized
    }

    @Test
    fun `preflight OPTIONS request should not return CORS headers for disallowed origin`() =
        runTest {
            webTestClient
                .options()
                .uri("/api/v1/facilities")
                .header(HttpHeaders.ORIGIN, "http://evil.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET.name())
                .exchange()
                .expectHeader()
                .doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
        }

    @Test
    fun `corsConfigurationSource should allow configured origin`() {
        val securityConfig = SecurityConfig(jwtAuthenticationManager, "http://localhost:8080")
        val source = securityConfig.corsConfigurationSource()
        val exchange =
            MockServerWebExchange.from(
                MockServerHttpRequest.get("http://localhost:8080/api/v1/facilities").build()
            )
        val config = source.getCorsConfiguration(exchange)
        assertThat(config).isNotNull()
        assertThat(config!!.allowedOrigins).contains("http://localhost:8080")
    }

    @Test
    fun `corsConfigurationSource should support multiple origins`() {
        val securityConfig =
            SecurityConfig(
                jwtAuthenticationManager,
                "http://localhost:8080,https://your-production-domain.com",
            )
        val source = securityConfig.corsConfigurationSource()
        val exchange =
            MockServerWebExchange.from(
                MockServerHttpRequest.get("http://localhost:8080/api/v1/facilities").build()
            )
        val config = source.getCorsConfiguration(exchange)
        assertThat(config).isNotNull()
        assertThat(config!!.allowedOrigins)
            .containsExactly(
                "http://localhost:8080",
                "https://your-production-domain.com",
            )
    }
}
