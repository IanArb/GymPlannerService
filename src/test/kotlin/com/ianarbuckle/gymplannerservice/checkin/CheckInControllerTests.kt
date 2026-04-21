package com.ianarbuckle.gymplannerservice.checkin

import com.ianarbuckle.gymplannerservice.checkin.data.CheckInService
import com.ianarbuckle.gymplannerservice.checkin.data.CheckInStatus
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedOutException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotFoundException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotScheduledException
import com.ianarbuckle.gymplannerservice.mocks.CheckInDataProvider
import java.time.LocalDateTime
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
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
    controllers = [CheckInController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class CheckInControllerTests {

    @Autowired private lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var checkInService: CheckInService

    @Test
    fun `should return 200 when check-in is valid`() = runTest {
        val checkIn = CheckInDataProvider.createCheckIn()
        val request = CheckInDataProvider.createCheckInRequest()
        Mockito.`when`(checkInService.checkIn("1", request.checkInTime)).thenReturn(checkIn)

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.trainerId")
            .isEqualTo("1")
            .jsonPath("$.status")
            .isEqualTo("ON_TIME")
    }

    @Test
    fun `should return 404 when trainer is not found`() = runTest {
        val request = CheckInDataProvider.createCheckInRequest()
        Mockito.`when`(checkInService.checkIn("999", request.checkInTime))
            .thenThrow(TrainerNotFoundException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/999/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should return 400 when trainer is not scheduled`() = runTest {
        val request = CheckInDataProvider.createCheckInRequest()
        Mockito.`when`(checkInService.checkIn("1", request.checkInTime))
            .thenThrow(TrainerNotScheduledException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return 400 when trainer has already checked in today`() = runTest {
        val request = CheckInDataProvider.createCheckInRequest()
        Mockito.`when`(checkInService.checkIn("1", request.checkInTime))
            .thenThrow(TrainerAlreadyCheckedInException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return 200 with LATE status when trainer checks in after shift start`() = runTest {
        val lateCheckIn =
            CheckInDataProvider.createCheckIn(
                checkInTime = LocalDateTime.of(2026, 4, 21, 10, 30),
                status = CheckInStatus.LATE,
            )
        val request =
            CheckInDataProvider.createCheckInRequest(
                checkInTime = LocalDateTime.of(2026, 4, 21, 10, 30),
            )
        Mockito.`when`(checkInService.checkIn("1", request.checkInTime)).thenReturn(lateCheckIn)

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo("LATE")
    }

    @Test
    fun `should return 200 when check-out is valid`() = runTest {
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
        val checkIn = CheckInDataProvider.createCheckIn(checkOutTime = checkOutTime)
        val request = CheckInDataProvider.createCheckOutRequest(checkOutTime = checkOutTime)
        Mockito.`when`(checkInService.checkOut("1", checkOutTime)).thenReturn(checkIn)

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-out")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.trainerId")
            .isEqualTo("1")
            .jsonPath("$.checkOutTime")
            .isNotEmpty
    }

    @Test
    fun `should return 404 on check-out when trainer is not found`() = runTest {
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
        val request = CheckInDataProvider.createCheckOutRequest(checkOutTime = checkOutTime)
        Mockito.`when`(checkInService.checkOut("999", checkOutTime))
            .thenThrow(TrainerNotFoundException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/999/check-out")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should return 400 when trainer has not checked in`() = runTest {
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
        val request = CheckInDataProvider.createCheckOutRequest(checkOutTime = checkOutTime)
        Mockito.`when`(checkInService.checkOut("1", checkOutTime))
            .thenThrow(TrainerNotCheckedInException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-out")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `should return 400 when trainer has already checked out`() = runTest {
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
        val request = CheckInDataProvider.createCheckOutRequest(checkOutTime = checkOutTime)
        Mockito.`when`(checkInService.checkOut("1", checkOutTime))
            .thenThrow(TrainerAlreadyCheckedOutException())

        webTestClient
            .post()
            .uri("/api/v1/trainers/1/check-out")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isBadRequest
    }
}
