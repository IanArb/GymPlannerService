import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.booking.data.BookingRepository
import com.ianarbuckle.gymplannerservice.fcm.FcmSender
import java.time.Clock
import java.time.LocalDateTime
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BookingReminderScheduler(
    private val bookingRepository: BookingRepository,
    private val user: UserRepository,
    private val fcmSender: FcmSender,
    private val clock: Clock
) {
    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    suspend fun sendReminders() {
        val now = LocalDateTime.now(clock)
        val reminderTime = now.plusDays(1).toLocalDate()
        val bookings = bookingRepository.findBookingsByBookingDate(reminderTime)
        bookings.collect { booking ->
            val user = user.findById(booking.userId) ?: return@collect
            val token = user.pushNotificationToken
            if (token.isNullOrEmpty()) return@collect

            fcmSender.sendMessage(
                token,
                "Booking Reminder",
                "You have a booking with ${booking.personalTrainer.name} on ${booking.bookingDate}"
            )
        }
    }
}
