package com.ianarbuckle.gymplannerservice.booking

import com.ianarbuckle.gymplannerservice.booking.data.BookingService
import com.ianarbuckle.gymplannerservice.booking.data.BookingStatus
import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
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
import org.springframework.web.reactive.function.BodyInserters


@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [BookingController::class], excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class BookingsControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var bookingService: BookingService

    @Test
    fun `fetchAllBookings should return all bookings`() = runTest {
        val firstBooking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        val secondBooking = BookingDataProvider.createBooking(id = "2", status = BookingStatus.CONFIRMED)
        `when`(bookingService.fetchAllBookings()).thenReturn(flowOf(firstBooking, secondBooking))

        webTestClient.get().uri("/api/v1/booking")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$[0].status").isEqualTo(BookingStatus.CONFIRMED.name)
    }

    @Test
    fun `fetchBookingById should return booking when found`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        `when`(bookingService.fetchBookingById("1")).thenReturn(booking)

        webTestClient.get().uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(BookingStatus.CONFIRMED.name)
    }

    @Test
    fun `fetchBookingById should return 404 when not found`() = runTest {
        `when`(bookingService.fetchBookingById("1")).thenReturn(null)

        webTestClient.get().uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `saveBooking should save booking when valid`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        `when`(bookingService.saveBooking(booking)).thenReturn(booking)

        val bookingJson = """
            {
                "id": ${booking.id},
                "client": {
                    "userId": "${booking.client.userId}",
                    "firstName": "${booking.client.firstName}",
                    "surname": "${booking.client.surname}",
                    "email": "${booking.client.email}",
                    "gymLocation": "${booking.client.gymLocation}"
                },
                "bookingDate": "${booking.bookingDate}",
                "startTime": "${booking.startTime}",
                "personalTrainer": {
                    "id": "${booking.personalTrainer.id}",
                    "firstName": "${booking.personalTrainer.firstName}",
                    "surname": "${booking.personalTrainer.surname}",
                    "imageUrl": "${booking.personalTrainer.imageUrl}",
                    "gymLocation": "${booking.personalTrainer.gymLocation}"
                },
                "status": "${booking.status}"
            }
        """.trimIndent()

        webTestClient.post().uri("/api/v1/booking")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(bookingJson))
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.status").isEqualTo(BookingStatus.CONFIRMED.name)
    }

    @Test
    fun `deleteBooking should delete booking by id`() = runTest {
        `when`(bookingService.deleteBookingById("1")).thenReturn(Unit)

        webTestClient.delete().uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }
}