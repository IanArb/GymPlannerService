package com.ianarbuckle.gymplannerservice.booking

import com.ianarbuckle.gymplannerservice.booking.data.BookingService
import com.ianarbuckle.gymplannerservice.booking.data.BookingStatus
import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
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
    controllers = [BookingController::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class],
)
@TestPropertySource("classpath:application-test.properties")
@ActiveProfiles("test")
@AutoConfigureDataMongo
class BookingsControllerTests {
    @Autowired lateinit var webTestClient: WebTestClient

    @MockitoBean private lateinit var bookingService: BookingService

    @Test
    fun `fetchAllBookings should return all bookings`() = runTest {
        val firstBooking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        val secondBooking =
            BookingDataProvider.createBooking(id = "2", status = BookingStatus.CONFIRMED)
        `when`(bookingService.fetchAllBookings()).thenReturn(flowOf(firstBooking, secondBooking))

        webTestClient
            .get()
            .uri("/api/v1/booking")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$[0].status")
            .isEqualTo(BookingStatus.CONFIRMED.name)
    }

    @Test
    fun `fetchBookingById should return booking when found`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        `when`(bookingService.fetchBookingById("1")).thenReturn(booking)

        webTestClient
            .get()
            .uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status")
            .isEqualTo(BookingStatus.CONFIRMED.name)
    }

    @Test
    fun `fetchBookingById should return 404 when not found`() = runTest {
        `when`(bookingService.fetchBookingById("1")).thenReturn(null)

        webTestClient
            .get()
            .uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `saveBooking should save booking when valid`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        `when`(bookingService.saveBooking(booking)).thenReturn(booking)

        webTestClient
            .post()
            .uri("/api/v1/booking")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(booking)
            .exchange()
            .expectStatus()
            .isCreated
    }

    @Test
    fun `deleteBooking should delete booking by id`() = runTest {
        `when`(bookingService.deleteBookingById("1")).thenReturn(Unit)

        webTestClient
            .delete()
            .uri("/api/v1/booking/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
    }
}
