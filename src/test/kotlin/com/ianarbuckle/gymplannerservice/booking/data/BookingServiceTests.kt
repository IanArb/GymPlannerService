package com.ianarbuckle.gymplannerservice.booking.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserAccountRepository
import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.data.BookingDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class BookingServiceTests {

    private val bookingsRepository = mockk<BookingRepository>()
    private val personalTrainersRepository = mockk<PersonalTrainerRepository>()
    private val userAccountRepository = mockk<UserAccountRepository>()
    private val bookingService = BookingServiceImpl(bookingsRepository, personalTrainersRepository, userAccountRepository)

    @Test
    fun `fetchAllBookings should return all bookings`() = runTest {
        val bookings = flowOf(BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED))
        coEvery { bookingsRepository.findAll() } returns bookings

        bookingService.fetchAllBookings().test {
            assertThat(awaitItem()).isEqualTo(bookings.first())
            awaitComplete()
        }

    }

    @Test
    fun `fetchBookingById should return booking when found`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        coEvery { bookingsRepository.findById("1") } returns booking

        val result = bookingService.fetchBookingById("1")

        assertThat(result).isEqualTo(booking)
    }

    @Test
    fun `fetchBookingById should return null when not found`() = runTest {
        coEvery { bookingsRepository.findById("1") } returns null

        val result = bookingService.fetchBookingById("1")

        assertThat(result).isNull()
    }

    @Test
    fun `findBookingsByPersonalTrainerId should throw exception when personal trainer not found`() = runTest {
        coEvery { personalTrainersRepository.findById("1") } returns null

        val exception = assertThrows<PersonalTrainerNotFoundException> {
            bookingService.findBookingsByPersonalTrainerId("1")
        }

        assertThat(exception).isInstanceOf(PersonalTrainerNotFoundException::class.java)
        assertWithMessage("Expected exception message")
            .that(exception).hasMessageThat().contains("Personal trainer not found")

    }

    @Test
    fun `findBookingsByPersonalTrainerId should throw exception when no bookings found`() = runTest {
        coEvery { personalTrainersRepository.findById("1") } returns mockk<PersonalTrainer>()
        coEvery { bookingsRepository.findBookingsByPersonalTrainerId("1") } returns flowOf()

        val exception = assertThrows<BookingsNotFoundException> {
            bookingService.findBookingsByPersonalTrainerId("1")
        }

        assertThat(exception).isInstanceOf(BookingsNotFoundException::class.java)
        assertWithMessage("Expected exception message")
            .that(exception).hasMessageThat().contains("Bookings not found")
    }

    @Test
    fun `saveBooking should throw exception when personal trainer already booked`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
        val personalTrainer = PersonalTrainer(
            id = booking.personalTrainer.id,
            firstName = booking.personalTrainer.firstName,
            lastName = booking.personalTrainer.surname,
            bio = "",
            imageUrl = booking.personalTrainer.imageUrl,
            gymLocation = booking.personalTrainer.gymLocation,
            qualifications = emptyList()
        )
        coEvery { personalTrainersRepository.findById(any()) } returns personalTrainer
        coEvery { bookingsRepository.findAll() } returns flowOf(booking)

        val exception = assertThrows<PersonalTrainerAlreadyBookedException> {
            bookingService.saveBooking(booking)
        }

        assertThat(exception).isInstanceOf(PersonalTrainerAlreadyBookedException::class.java)
        assertWithMessage("Expected exception message")
            .that(exception).hasMessageThat().contains("This personal trainer is already booked at the specified date and time")
    }

    @Test
    fun `saveBooking should save booking when valid`() = runTest {
        val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)

        coEvery { personalTrainersRepository.findById(any()) } returns PersonalTrainer(
            id = booking.personalTrainer.id,
            firstName = booking.personalTrainer.firstName,
            lastName = booking.personalTrainer.surname,
            bio = "",
            imageUrl = booking.personalTrainer.imageUrl,
            gymLocation = booking.personalTrainer.gymLocation,
            qualifications = emptyList()
        )
        coEvery { bookingsRepository.findAll() } returns flowOf()
        coEvery { bookingsRepository.save(booking) } returns booking

        val result = bookingService.saveBooking(booking)

        assertThat(result).isEqualTo(booking)
    }
}