package com.ianarbuckle.gymplannerservice.facilityStatus

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import com.ianarbuckle.gymplannerservice.facilityStatus.data.MachineStatus
import com.ianarbuckle.gymplannerservice.mocks.FacilityStatusDataProvider
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(
    controllers = [FacilityStatusController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class FacilityStatusControllerTests {
    @Autowired private lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var facilityStatusService: FacilityStatusService

    @Test
    fun `should return all machines`() = runTest {
        // Given
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    machineNumber = 2,
                ),
            )
        `when`(facilityStatusService.findAllMachines())
            .thenReturn(flowOf(*facilities.toTypedArray()))

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/facilities")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$[0].machineName")
            .isEqualTo("Treadmill")
            .jsonPath("$[1].machineName")
            .isEqualTo("Rowing Machine")
    }

    @Test
    fun `should return facilities by gym location`() = runTest {
        // Given
        val gymLocation = GymLocation.CLONTARF
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(gymLocation = gymLocation),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    gymLocation = gymLocation,
                ),
            )
        `when`(facilityStatusService.findMachinesByGymLocation(gymLocation))
            .thenReturn(flowOf(*facilities.toTypedArray()))

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/facilities?gymLocation=${gymLocation.name}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$[0].gymLocation")
            .isEqualTo(gymLocation.name)
            .jsonPath("$[1].gymLocation")
            .isEqualTo(gymLocation.name)
    }

    @Test
    fun `should return machine by id`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        `when`(facilityStatusService.findMachineById("1")).thenReturn(facility)

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/facilities/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.machineName")
            .isEqualTo("Treadmill")
            .jsonPath("$.machineNumber")
            .isEqualTo(1)
    }

    @Test
    fun `should return 404 when machine not found by id`() = runTest {
        // Given
        `when`(facilityStatusService.findMachineById("1")).thenReturn(null)

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/facilities/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should update facility`() = runTest {
        // Given
        val facility =
            FacilityStatusDataProvider.createFacilityStatus(status = MachineStatus.OUT_OF_ORDER)
        `when`(facilityStatusService.findMachineById("1")).thenReturn(facility)
        `when`(facilityStatusService.updateFacility(facility)).thenReturn(facility)

        // When & Then
        webTestClient
            .put()
            .uri("/api/v1/facilities/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(facility)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(FacilityStatus::class.java)
            .isEqualTo(facility)
    }

    @Test
    fun `should return 404 when updating machine that does not exist`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        `when`(facilityStatusService.findMachineById("999")).thenReturn(null)

        // When & Then
        webTestClient
            .put()
            .uri("/api/v1/facilities/999")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(facility)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should delete facility by id`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        `when`(facilityStatusService.findMachineById("1")).thenReturn(facility)
        `when`(facilityStatusService.deleteFacilityById("1")).thenReturn(Unit)

        // When & Then
        webTestClient.delete().uri("/api/v1/facilities/1").exchange().expectStatus().isNoContent
    }

    @Test
    fun `should return 404 when deleting machine that does not exist`() = runTest {
        // Given
        `when`(facilityStatusService.findMachineById("999")).thenReturn(null)

        // When & Then
        webTestClient.delete().uri("/api/v1/facilities/999").exchange().expectStatus().isNotFound
    }

    @Test
    fun `should create facility`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        `when`(facilityStatusService.saveFacility(facility)).thenReturn(Unit)

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/facilities")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(facility)
            .exchange()
            .expectStatus()
            .isCreated
    }

    @Test
    fun `should create batch of facilities`() = runTest {
        // Given
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    machineNumber = 2,
                ),
            )
        `when`(facilityStatusService.saveAllFacilities(facilities)).thenReturn(Unit)

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/facilities/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(facilities)
            .exchange()
            .expectStatus()
            .isCreated
    }

    @Test
    fun `should delete all facilities by gym location`() = runTest {
        // Given
        val gymLocation = GymLocation.CLONTARF
        `when`(facilityStatusService.deleteAllFacilitiesByGymLocation(gymLocation)).thenReturn(Unit)

        // When & Then
        webTestClient
            .delete()
            .uri("/api/v1/facilities/batch?gymLocation=${gymLocation.name}")
            .exchange()
            .expectStatus()
            .isNoContent
    }

    @Test
    fun `should return 400 when creating facility with invalid status`() = runTest {
        // Given
        val invalidBody =
            """
            {
                "machineName": "Treadmill",
                "machineNumber": 1,
                "gymLocation": "CLONTARF",
                "location": "MAIN_GYM_FLOOR",
                "faultType": "MECHANICAL",
                "status": "INVALID_STATUS"
            }
            """.trimIndent()

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/facilities")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidBody)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.error")
            .isNotEmpty
    }

    @Test
    fun `should return 400 when creating facility with invalid location`() = runTest {
        // Given
        val invalidBody =
            """
            {
                "machineName": "Treadmill",
                "machineNumber": 1,
                "gymLocation": "CLONTARF",
                "location": "INVALID_LOCATION",
                "faultType": "MECHANICAL",
                "status": "OPERATIONAL"
            }
            """.trimIndent()

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/facilities")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidBody)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.error")
            .isNotEmpty
    }

    @Test
    fun `should return 400 when creating batch with invalid status`() = runTest {
        // Given
        val invalidBody =
            """
            [
                {
                    "machineName": "Treadmill",
                    "machineNumber": 1,
                    "gymLocation": "CLONTARF",
                    "location": "MAIN_GYM_FLOOR",
                    "faultType": "MECHANICAL",
                    "status": "INVALID_STATUS"
                }
            ]
            """.trimIndent()

        // When & Then
        webTestClient
            .post()
            .uri("/api/v1/facilities/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidBody)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody()
            .jsonPath("$.error")
            .isNotEmpty
    }

    @Test
    fun `should return facilities by status`() = runTest {
        // Given
        val status = MachineStatus.OPERATIONAL.name
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(status = MachineStatus.OPERATIONAL),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    status = MachineStatus.OPERATIONAL,
                ),
            )
        `when`(facilityStatusService.findMachinesByStatus(status))
            .thenReturn(flowOf(*facilities.toTypedArray()))

        // When & Then
        webTestClient
            .get()
            .uri("/api/v1/facilities/status/$status")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$[0].status")
            .isEqualTo(MachineStatus.OPERATIONAL.name)
            .jsonPath("$[1].status")
            .isEqualTo(MachineStatus.OPERATIONAL.name)
    }
}
