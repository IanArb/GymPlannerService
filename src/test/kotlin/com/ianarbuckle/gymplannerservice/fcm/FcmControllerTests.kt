package com.ianarbuckle.gymplannerservice.fcm

import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenRequest
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenResponse
import com.ianarbuckle.gymplannerservice.fcm.data.FcmTokenService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.mongodb.test.autoconfigure.AutoConfigureDataMongo
import org.springframework.boot.security.autoconfigure.web.reactive.ReactiveWebSecurityAutoConfiguration
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [FcmController::class],
    excludeAutoConfiguration = [ReactiveWebSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class FcmControllerTests {
    @Autowired lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var fcmTokenService: FcmTokenService

    @Test
    fun `should register push notification token successfully`() =
        runTest {
            val tokenRequest = FcmTokenRequest(userId = "user123", token = "fcm-token-123")
            val fcmTokenResponse = FcmTokenResponse(tokenRequest.userId)
            given(fcmTokenService.registerToken(tokenRequest.userId, tokenRequest.token))
                .willReturn(fcmTokenResponse)

            webTestClient
                .post()
                .uri("/api/v1/fcm/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(tokenRequest)
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `should delete push notification token successfully`() =
        runTest {
            val userId = "user123"
            given(fcmTokenService.deleteToken(userId)).willReturn(Unit)

            webTestClient
                .delete()
                .uri("/api/v1/fcm/delete/$userId")
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `should return bad request for invalid token request`() =
        runTest {
            val invalidRequest = mapOf("invalid" to "request")

            webTestClient
                .post()
                .uri("/api/v1/fcm/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus()
                .isBadRequest
        }
}
