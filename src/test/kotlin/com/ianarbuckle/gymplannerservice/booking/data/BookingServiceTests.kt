package com.ianarbuckle.gymplannerservice.booking.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import com.ianarbuckle.gymplannerservice.mocks.UserProfileDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileRepository
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
    private val userProfileRepository = mockk<UserProfileRepository>()
    private val bookingService = BookingServiceImpl(bookingsRepository, personalTrainersRepository, userProfileRepository)

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
            coEvery { personalTrainersRepository.findById("1") } returns null

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
            coEvery { personalTrainersRepository.findById("1") } returns mockk<PersonalTrainer>()
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
            val personalTrainer =
                PersonalTrainer(
                    id = booking.personalTrainer.id,
                    firstName = booking.personalTrainer.firstName,
                    lastName = booking.personalTrainer.surname,
                    bio = "",
                    imageUrl = booking.personalTrainer.imageUrl,
                    gymLocation = booking.personalTrainer.gymLocation,
                    qualifications = emptyList(),
                )
            coEvery { personalTrainersRepository.findById(any()) } returns personalTrainer
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
    fun `saveBooking should save booking when valid`() =
        runTest {
            val booking = BookingDataProvider.createBooking(status = BookingStatus.CONFIRMED)

            coEvery { personalTrainersRepository.findById(any()) } returns
                PersonalTrainer(
                    id = booking.personalTrainer.id,
                    firstName = booking.personalTrainer.firstName,
                    lastName = booking.personalTrainer.surname,
                    bio = "",
                    imageUrl = booking.personalTrainer.imageUrl,
                    gymLocation = booking.personalTrainer.gymLocation,
                    qualifications = emptyList(),
                )
            coEvery { bookingsRepository.findAll() } returns flowOf()
            coEvery { bookingsRepository.save(booking) } returns booking

            val result = bookingService.saveBooking(booking)

            assertThat(result).isEqualTo(booking)
        }

    @Test
    fun `findBookingsByUserId should return bookings`() =
        runTest {
            val userId = "123456"
            val booking =
                BookingDataProvider.createBooking(
                    status = BookingStatus.CONFIRMED,
                    client = BookingDataProvider.createClient(userId = userId),
                )

            coEvery { userProfileRepository.findByUserId(userId) } returns UserProfileDataProvider.createUserProfile()
            coEvery { bookingsRepository.findBookingsByClientUserId(userId) } returns flowOf(booking)

            bookingService.findBookingsByUserId(userId).test {
                assertThat(awaitItem()).isEqualTo(booking)
                awaitComplete()
            }
        }

    @Test
    fun `findBookingsByUserId should throw UserNotFoundException when user profile not found`() =
        runTest {
            val userId = "nonexistentUserId"
            coEvery { userProfileRepository.findByUserId(userId) } returns null

            val exception =
                assertThrows<UserNotFoundException> {
                    bookingService.findBookingsByUserId(userId)
                }

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
            coEvery { userProfileRepository.findByUserId(userId) } returns UserProfileDataProvider.createUserProfile()
            coEvery { bookingsRepository.findBookingsByClientUserId(userId) } returns flowOf()

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
