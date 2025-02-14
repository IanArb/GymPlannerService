package com.ianarbuckle.gymplannerservice.availability

import com.ianarbuckle.gymplannerservice.availability.data.AvailabilityService
import com.ianarbuckle.gymplannerservice.availability.data.CheckAvailability
import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.AvailabilityDataProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
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
    controllers = [AvailabilityController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class AvailabilityControllerTests {
    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var availabilityService: AvailabilityService

    @Test
    fun `should return availability when found`() =
        runTest {
            val availability = AvailabilityDataProvider.createAvailability()
            given(availabilityService.getAvailability(availability.personalTrainerId, availability.month))
                .willReturn(availability)

            webTestClient
                .get()
                .uri("/api/v1/availability/${availability.personalTrainerId}/${availability.month}")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.personalTrainerId")
                .isEqualTo(availability.personalTrainerId)
                .jsonPath("$.month")
                .isEqualTo(availability.month)
        }

    @Test
    fun `should return 404 when availability not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            given(availabilityService.getAvailability(personalTrainerId, month))
                .willThrow(AvailabilityNotFoundException::class.java)

            webTestClient
                .get()
                .uri("/api/v1/availability/$personalTrainerId/$month")
                .exchange()
                .expectStatus()
                .isNotFound
        }

    @Test
    fun `should return 404 when personal trainer not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            given(availabilityService.getAvailability(personalTrainerId, month))
                .willThrow(PersonalTrainerNotFoundException::class.java)

            webTestClient
                .get()
                .uri("/api/v1/availability/$personalTrainerId/$month")
                .exchange()
                .expectStatus()
                .isNotFound
        }

    @Test
    fun `should save availability`() =
        runTest {
            val availability = AvailabilityDataProvider.createAvailability()
            given(availabilityService.saveAvailability(availability)).willReturn(availability)

            webTestClient
                .post()
                .uri("/api/v1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(availability)
                .exchange()
                .expectStatus()
                .isCreated
        }

    @Test
    fun `should update availability`() {
        val availability = AvailabilityDataProvider.createAvailability()

        webTestClient
            .put()
            .uri("/api/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(availability)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `should delete availability`() {
        val personalTrainerId = "trainer1"

        webTestClient
            .delete()
            .uri("/api/v1/availability/$personalTrainerId")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `should return availability status`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            val date = "2023-12-01"
            val time = "08:00"
            val checkAvailability = CheckAvailability(personalTrainerId, true)
            given(availabilityService.isAvailable(personalTrainerId, month))
                .willReturn(checkAvailability)

            val uri = "/api/v1/availability/check_availability"

            webTestClient
                .get()
                .uri(
                    uri
                        .plus("?personalTrainerId=$personalTrainerId")
                        .plus("&month=$month")
                        .plus("&date=$date")
                        .plus("&time=$time")
                )
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("$.personalTrainerId")
                .isEqualTo(personalTrainerId)
                .jsonPath("$.isAvailable")
                .isEqualTo(true)
        }
}
