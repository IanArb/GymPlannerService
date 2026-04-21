package com.ianarbuckle.gymplannerservice.checkin.data

import java.time.LocalDateTime
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckInRepository : CoroutineCrudRepository<CheckIn, String> {

    suspend fun findByTrainerIdAndCheckInTimeBetween(
        trainerId: String,
        start: LocalDateTime,
        end: LocalDateTime,
    ): CheckIn?
}
