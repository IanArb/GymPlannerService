package com.ianarbuckle.gymplannerservice.booking

import com.ianarbuckle.gymplannerservice.booking.data.Booking
import com.ianarbuckle.gymplannerservice.booking.data.BookingService
import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/booking")
@Tag(name = "Booking", description = "Endpoints for booking")
class BookingController(
    private val service: BookingService,
) {
    @GetMapping
    suspend fun fetchAllBookings(): Flow<Booking> =
        try {
            service.fetchAllBookings()
        } catch (ex: BookingsNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Bookings not found",
                ex,
            )
        }

    @GetMapping("/{id}")
    suspend fun findBookingById(
        @PathVariable id: String,
    ): Booking? =
        service.fetchBookingById(id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Booking not found",
        )

    @GetMapping("/personal-trainer/{id}")
    suspend fun findBookingsByPersonalTrainer(
        @PathVariable id: String,
    ): Flow<Booking> =
        try {
            service.findBookingsByPersonalTrainerId(id)
        } catch (ex: BookingsNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Bookings not found",
                ex,
            )
        }

    @GetMapping("/user/{id}")
    suspend fun findBookingsByUser(
        @PathVariable id: String,
    ): Flow<Booking> =
        try {
            service.findBookingsByUserId(id)
        } catch (ex: BookingsNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Bookings not found",
                ex,
            )
        }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveBooking(
        @RequestBody booking: Booking,
    ): Booking =
        try {
            service.saveBooking(booking)
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Personal trainer not found",
                ex,
            )
        } catch (ex: PersonalTrainerAlreadyBookedException) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Personal trainer already booked",
                ex,
            )
        }

    @PutMapping
    suspend fun updateBooking(
        @RequestBody booking: Booking,
    ) = service.updateBooking(booking)

    @DeleteMapping("/{id}")
    suspend fun deleteBooking(
        @PathVariable id: String,
    ) = service.deleteBookingById(id)
}
