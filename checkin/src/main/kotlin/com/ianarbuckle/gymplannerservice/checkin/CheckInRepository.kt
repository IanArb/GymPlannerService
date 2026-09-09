package com.ianarbuckle.gymplannerservice.checkin

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CheckInRepository : CoroutineCrudRepository<CheckIn, String> {
    suspend fun findByTrainerIdAndCheckInTimeBetween(
        trainerId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): CheckIn?
}
