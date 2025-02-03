package com.ianarbuckle.gymplannerservice.userProfile

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.UserProfileDataProvider
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [UserProfileController::class], excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class UserProfileControllerTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var userProfileService: UserProfileService

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `userProfile should return UserProfile when found`() =
        runTest {
            val userId = "123456"
            val userProfile = UserProfileDataProvider.createUserProfile(userId = userId)
            `when`(userProfileService.userProfile(userId)).thenReturn(userProfile)

            webTestClient
                .get()
                .uri("/api/v1/user_profile/$userId")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.userId")
                .isEqualTo(userId)
                .jsonPath("$.username")
                .isEqualTo("username")
                .jsonPath("$.firstName")
                .isEqualTo("firstName")
                .jsonPath("$.surname")
                .isEqualTo("surname")
                .jsonPath("$.email")
                .isEqualTo("email")
        }

    @Test
    fun `userProfile should return 404 when UserProfile not found`() =
        runTest {
            val userId = "nonexistentUserId"
            Mockito.`when`(userProfileService.userProfile(userId)).thenThrow(UserNotFoundException())

            webTestClient
                .get()
                .uri("/api/v1/user_profile/$userId")
                .exchange()
                .expectStatus()
                .isNotFound
        }

    @Test
    fun `updateUserProfile should return 200 when UserProfile is updated`() =
        runTest {
            val userId = "123456"
            val userProfile = UserProfileDataProvider.createUserProfile(userId = userId)
            `when`(userProfileService.updateUserProfile(userProfile)).thenReturn(Unit)

            webTestClient
                .put()
                .uri("/api/v1/user_profile")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(userProfile))
                .exchange()
                .expectStatus()
                .isOk
        }

    @Test
    fun `updateUserProfile should return 404 when UserProfile not found`() =
        runTest {
            val userId = "123456"
            val userProfile = UserProfileDataProvider.createUserProfile(userId = userId)
            `when`(userProfileService.updateUserProfile(userProfile)).thenThrow(UserNotFoundException())

            webTestClient
                .put()
                .uri("/api/v1/user_profile")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(userProfile))
                .exchange()
                .expectStatus()
                .isNotFound
        }
}
