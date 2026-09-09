package com.ianarbuckle.gymplannerservice.booking

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.common.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.common.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.common.UserNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.AvailabilityDataProvider
import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class BookingServiceTests {
    private val bookingsRepository = mockk<BookingRepository>()
    private val personalTrainerGateway = mockk<PersonalTrainerGateway>()
    private val userProfileGateway = mockk<UserProfileGateway>()
    private val availabilityGateway = mockk<AvailabilityGateway>()

    private val bookingService =
        BookingServiceImpl(
            bookingsRepository = bookingsRepository,
            personalTrainerGateway = personalTrainerGateway,
            userProfileGateway = userProfileGateway,
            availabilityGateway = availabilityGateway,
        )

    @Test
    fun `fetchAllBookings should return all bookings`() =
        runTest {
            val bookings = flowOf(BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED))
            coEvery { bookingsRepository.findAll() } returns bookings

            bookingService.fetchAllBookings().test {
                assertThat(awaitItem()).isEqualTo(bookings.first())
                awaitComplete()
            }
        }

    @Test
    fun `fetchBookingById should return booking when found`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
            coEvery { bookingsRepository.findById("1") } returns booking

            val result = bookingService.fetchBookingById("1")

            assertThat(result).isEqualTo(booking)
        }

    @Test
    fun `fetchBookingById should return null when not found`() =
        runTest {
            coEvery { bookingsRepository.findById("1") } returns null

            val result = bookingService.fetchBookingById("1")

            assertThat(result).isNull()
        }

    @Test
    fun `findBookingsByPersonalTrainerId should throw exception when personal trainer not found`() =
        runTest {
            coEvery { personalTrainerGateway.existsById("1") } returns false

            val exception =
                assertThrows<PersonalTrainerNotFoundException> {
                    bookingService.findBookingsByPersonalTrainerId("1")
                }

            assertThat(exception).isInstanceOf(PersonalTrainerNotFoundException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("Personal trainer not found")
        }

    @Test
    fun `findBookingsByPersonalTrainerId should throw exception when no bookings found`() =
        runTest {
            coEvery { personalTrainerGateway.existsById("1") } returns true
            coEvery { bookingsRepository.findBookingsByPersonalTrainerId("1") } returns flowOf()

            val exception =
                assertThrows<BookingsNotFoundException> {
                    bookingService.findBookingsByPersonalTrainerId("1")
                }

            assertThat(exception).isInstanceOf(BookingsNotFoundException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("Bookings not found")
        }

    @Test
    fun `saveBooking should throw exception when personal trainer already booked`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)
            coEvery { personalTrainerGateway.existsById(any()) } returns true
            coEvery { bookingsRepository.findAll() } returns flowOf(booking)

            val exception =
                assertThrows<PersonalTrainerAlreadyBookedException> {
                    bookingService.saveBooking(booking)
                }

            assertThat(exception).isInstanceOf(PersonalTrainerAlreadyBookedException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("This personal trainer is already booked at the specified date and time")
        }

    @Test
    fun `saveBooking should save booking and update availability time slot`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)

            coEvery { personalTrainerGateway.existsById(any()) } returns true
            coEvery { bookingsRepository.findAll() } returns flowOf()
            coEvery { bookingsRepository.save(booking) } returns booking

            coEvery { availabilityGateway.save(any()) } returns
                AvailabilityDataProvider.createAvailability()
            coEvery { availabilityGateway.findByTimeId(any()) } returns
                AvailabilityDataProvider.createAvailability(
                    timeSlotId = "1",
                )

            val result = bookingService.saveBooking(booking)

            coVerify(exactly = 1) { availabilityGateway.save(any()) }

            assertThat(result).isEqualTo(booking)
        }

    @Test
    fun `saveBooking should save booking and not update availability time slot`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)

            coEvery { personalTrainerGateway.existsById(any()) } returns true
            coEvery { bookingsRepository.findAll() } returns flowOf()
            coEvery { bookingsRepository.save(booking) } returns booking

            coEvery { availabilityGateway.save(any()) } returns
                AvailabilityDataProvider.createAvailability()
            coEvery { availabilityGateway.findByTimeId(any()) } returns
                AvailabilityDataProvider.createAvailability(
                    timeSlotId = "2",
                )

            val result = bookingService.saveBooking(booking)

            coVerify(exactly = 0) { availabilityGateway.save(any()) }

            assertThat(result).isEqualTo(booking)
        }

    @Test
    fun `saveBooking should throw AvailabilityNotFound exception when availability is not found`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)

            coEvery { personalTrainerGateway.existsById(any()) } returns true
            coEvery { bookingsRepository.findAll() } returns flowOf()
            coEvery { bookingsRepository.save(booking) } returns booking

            coEvery { availabilityGateway.save(any()) } returns
                AvailabilityDataProvider.createAvailability()
            coEvery { availabilityGateway.findByTimeId(any()) } returns null

            val exception =
                assertThrows<AvailabilityNotFoundException> { bookingService.saveBooking(booking) }

            assertThat(exception).isInstanceOf(AvailabilityNotFoundException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("Availability not found")
        }

    @Test
    fun `findBookingsByUserId should return bookings`() =
        runTest {
            val userId = "123456"
            val booking =
                BookingDataProvider.createBooking(
                    status = BookingStatus.CONFIRMED,
                )

            coEvery { userProfileGateway.existsByUserId(userId) } returns true
            coEvery { bookingsRepository.findBookingsByUserId(userId) } returns flowOf(booking)

            bookingService.findBookingsByUserId(userId).test {
                assertThat(awaitItem()).isEqualTo(booking)
                awaitComplete()
            }
        }

    @Test
    fun `findBookingsByUserId should throw UserNotFoundException when user profile not found`() =
        runTest {
            val userId = "nonexistentUserId"
            coEvery { userProfileGateway.existsByUserId(userId) } returns false

            val exception =
                assertThrows<UserNotFoundException> { bookingService.findBookingsByUserId(userId) }

            assertThat(exception).isInstanceOf(UserNotFoundException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("User not found")
        }

    @Test
    fun `findBookingsByUserId should throw BookingsNotFoundException when bookings are empty`() =
        runTest {
            val userId = "nonexistentUserId"
            coEvery { userProfileGateway.existsByUserId(userId) } returns true
            coEvery { bookingsRepository.findBookingsByUserId(userId) } returns flowOf()

            val exception =
                assertThrows<BookingsNotFoundException> {
                    bookingService.findBookingsByUserId(userId)
                }

            assertThat(exception).isInstanceOf(BookingsNotFoundException::class.java)
            assertWithMessage("Expected exception message")
                .that(exception)
                .hasMessageThat()
                .contains("Bookings not found")
        }
}
