package com.ianarbuckle.gymplannerservice.booking

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class BookingReminderScheduler(
    private val bookingRepository: BookingRepository,
    private val userGateway: UserGateway,
    private val notificationSender: NotificationSender,
    private val clock: Clock,
) {
    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    suspend fun sendReminders() {
        val now = LocalDateTime.now(clock)
        val reminderTime = now.plusDays(1).toLocalDate()
        val bookings = bookingRepository.findBookingsByBookingDate(reminderTime)
        bookings.collect { booking ->
            val token = userGateway.findPushNotificationToken(booking.userId)
            if (token.isNullOrEmpty()) return@collect

            notificationSender.sendMessage(
                token,
                "Booking Reminder",
                "You have a booking with ${booking.personalTrainer.name} on ${booking.bookingDate}",
            )
        }
    }
}
