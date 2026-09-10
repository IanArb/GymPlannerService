package com.ianarbuckle.gymplannerservice.booking

import com.ianarbuckle.gymplannerservice.mocks.BookingDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset

class BookingReminderSchedulerTest {
    private val bookingRepository = mockk<BookingRepository>()
    private val userGateway = mockk<UserGateway>()
    private val notificationSender = mockk<NotificationSender>(relaxed = true)
    private val fixedClock =
        Clock.fixed(LocalDateTime.of(2025, 6, 13, 10, 0).toInstant(ZoneOffset.UTC), ZoneOffset.UTC)

    private val scheduler =
        BookingReminderScheduler(bookingRepository, userGateway, notificationSender, fixedClock)

    @Test
    fun `should send reminders for bookings 1 day in advance`() =
        runTest {
            val booking = BookingDataProvider.createBooking()

            coEvery { bookingRepository.findBookingsByBookingDate(any()) } returns flowOf(booking)
            coEvery { userGateway.findPushNotificationToken(any()) } returns "pushToken"

            scheduler.sendReminders()

            coVerify { notificationSender.sendMessage(any(), any(), any()) }
        }

    @Test
    fun `should not send reminders for bookings 1 day in advance if not push token available`() =
        runTest {
            val booking = BookingDataProvider.createBooking()

            coEvery { bookingRepository.findBookingsByBookingDate(any()) } returns flowOf(booking)
            coEvery { userGateway.findPushNotificationToken(any()) } returns ""

            scheduler.sendReminders()

            coVerify(exactly = 0) { notificationSender.sendMessage(any(), any(), any()) }
        }
}
