package com.ianarbuckle.gymplannerservice.gymlocations

import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocation
import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocationsService
import com.ianarbuckle.gymplannerservice.mocks.GymLocationsProvider
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
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
@WebFluxTest(
    controllers = [GymLocationsController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class GymLocationsControllerTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockBean private lateinit var gymLocationsService: GymLocationsService

    @Test
    fun `should return all gym locations`() = runTest {
        // Given
        val gymLocations = GymLocationsProvider.gymLocations()
        `when`(gymLocationsService.findAllGymLocations()).thenReturn(gymLocations)

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/gym_locations")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$[0].id")
            .isEqualTo(gymLocations.first().id ?: "")
    }

    @Test
    fun `should save gym location`() = runTest {
        val gymLocation = GymLocationsProvider.createGymLocation()

        // Given
        `when`(gymLocationsService.saveGymLocation(gymLocation)).thenReturn(gymLocation)

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/gym_locations")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(gymLocation)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(GymLocation::class.java)
            .isEqualTo(gymLocation)
    }

    @Test
    fun `should delete gym location by id`() = runTest {
        // Given
        `when`(gymLocationsService.deleteGymLocationById("1")).thenReturn(Unit)

        // When & Then
        webTestClient.delete().uri("/api/v1/gym_locations/1").exchange().expectStatus().isOk
    }
}
