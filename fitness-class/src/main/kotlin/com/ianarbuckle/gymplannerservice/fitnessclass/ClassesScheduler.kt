package com.ianarbuckle.gymplannerservice.fitnessclass

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime

@Component
class ClassesScheduler(
    private val notificationSender: NotificationSender,
    private val classesService: FitnessClassesService,
    private val userGateway: UserGateway,
    private val clock: Clock,
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

                userGateway.findAllPushNotificationTokens().collect { token ->
                    notificationSender.sendMessage(
                        token = token,
                        title = title,
                        body = message,
                    )
                }
            }
        }
    }
}
