package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.fcm.FcmSender
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import java.time.Clock
import java.time.LocalDateTime
import kotlinx.coroutines.flow.filter
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ClassesScheduler(
    private val fcmSender: FcmSender,
    private val classesService: FitnessClassesService,
    private val userRepository: UserRepository,
    private val clock: Clock
) {

    // Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    suspend fun sendClassReminders() {
        val now = LocalDateTime.now(clock)
        val oneHourLater = now.plusHours(1)
        val classesToday = classesService.fitnessClasses()
        classesToday.collect { fitnessClass ->
            val classStartTime = fitnessClass.startTime

            if (classStartTime.hour == oneHourLater.hour) {
                val title = "Class Reminder"
                val message = "${fitnessClass.name} class starts in 1 hour at $classStartTime"

                val users =
                    userRepository.findAll().filter {
                        it.pushNotificationToken != null && it.pushNotificationToken.isNotEmpty()
                    }

                users.collect { user ->
                    val pushNotificationToken = user.pushNotificationToken
                    if (pushNotificationToken != null) {
                        fcmSender.sendMessage(
                            token = pushNotificationToken,
                            title = title,
                            body = message,
                        )
                    }
                }
            }
        }
    }
}
