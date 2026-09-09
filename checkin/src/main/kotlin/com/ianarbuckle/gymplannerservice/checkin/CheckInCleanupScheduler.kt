package com.ianarbuckle.gymplannerservice.checkin

import com.ianarbuckle.gymplannerservice.checkin.data.CheckInRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckInCleanupScheduler(
    private val checkInRepository: CheckInRepository,
) {
    // Runs at midnight on the last day of every month
    @Scheduled(cron = "0 0 0 L * *")
    suspend fun deleteAllCheckIns() {
        checkInRepository.deleteAll()
    }
}
