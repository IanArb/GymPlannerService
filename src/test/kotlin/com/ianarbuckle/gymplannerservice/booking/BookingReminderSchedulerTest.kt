package com.ianarbuckle.gymplannerservice.booking

import BookingReminderScheduler
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.booking.data.BookingRepository
import com.ianarbuckle.gymplannerservice.fcm.FcmSender
import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import com.ianarbuckle.gymplannerservice.mocks.UserDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class BookingReminderSchedulerTest {
    private val bookingRepository = mockk<BookingRepository>()
    private val userRepository = mockk<UserRepository>()
    private val fcmSender = mockk<FcmSender>(relaxed = true)
    private val fixedClock =
        Clock.fixed(LocalDateTime.of(2025, 6, 13, 10, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

    private val scheduler =
        BookingReminderScheduler(bookingRepository, userRepository, fcmSender, fixedClock)

    @Test
    fun `should send reminders for bookings 1 day in advance`() = runTest {
        val booking = BookingDataProvider.createBooking()
        val user = UserDataProvider.createUser()

        coEvery { bookingRepository.findBookingsByBookingDate(any()) } returns flowOf(booking)
        coEvery { userRepository.findById(any()) } returns user

        scheduler.sendReminders()

        coVerify { fcmSender.sendMessage(any(), any(), any()) }
    }

    @Test
    fun `should not send reminders for bookings 1 day in advance if not push token available`() =
        runTest {
            val booking = BookingDataProvider.createBooking()
            val user = UserDataProvider.createUser(pushNotificationToken = "")

            coEvery { bookingRepository.findBookingsByBookingDate(any()) } returns flowOf(booking)
            coEvery { userRepository.findById(any()) } returns user

            scheduler.sendReminders()

            coVerify(exactly = 0) { fcmSender.sendMessage(any(), any(), any()) }
        }
}
