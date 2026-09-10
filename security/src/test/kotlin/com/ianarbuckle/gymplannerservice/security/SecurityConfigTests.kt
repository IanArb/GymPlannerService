package com.ianarbuckle.gymplannerservice.security

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [TestSecuredController::class])
@Import(SecurityConfig::class, JwtServerAuthenticationConverter::class)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
class SecurityConfigTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var jwtAuthenticationManager: JWTAuthenticationManager

    @Test
    fun `passwordEncoder should be BCryptPasswordEncoder`() {
        val securityConfig = SecurityConfig(jwtAuthenticationManager, "http://localhost:8080")
        assertThat(securityConfig.passwordEncoder()).isInstanceOf(BCryptPasswordEncoder::class.java)
    }

    @Test
    fun `auth endpoints should be accessible without token`() =
        runTest {
            webTestClient
                .post()
                .uri("/api/v1/auth/register")
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `protected endpoints should return 401 without token`() =
        runTest {
            webTestClient
                .get()
                .uri("/api/v1/facilities")
                .exchange()
                .expectStatus()
                .isUnauthorized
        }

    @Test
    fun `users with ROLE_USER should be forbidden from facilities`() =
        runTest {
            `when`(jwtAuthenticationManager.authenticate(anyAuthentication()))
                .thenReturn(authenticationFor("ROLE_USER"))

            webTestClient
                .get()
                .uri("/api/v1/facilities")
                .header(HttpHeaders.AUTHORIZATION, "Bearer faketoken")
                .exchange()
                .expectStatus()
                .isForbidden
        }

    @Test
    fun `users with ROLE_ADMIN should be allowed on facilities`() =
        runTest {
            `when`(jwtAuthenticationManager.authenticate(anyAuthentication()))
                .thenReturn(authenticationFor("ROLE_ADMIN"))

            webTestClient
                .get()
                .uri("/api/v1/facilities")
                .header(HttpHeaders.AUTHORIZATION, "Bearer faketoken")
                .exchange()
                .expectStatus()
                .isOk
        }

    private fun authenticationFor(authority: String): Mono<Authentication> =
        Mono.just(
            UsernamePasswordAuthenticationToken(
                "user",
                "password",
                listOf(SimpleGrantedAuthority(authority)),
            ),
        )

    private fun anyAuthentication(): Authentication = anyKotlin()

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyKotlin(): T {
        ArgumentMatchers.any<T>()
        return null as T
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
                MockServerHttpRequest.get("http://localhost:8080/api/v1/facilities").build(),
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
                MockServerHttpRequest.get("http://localhost:8080/api/v1/facilities").build(),
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
