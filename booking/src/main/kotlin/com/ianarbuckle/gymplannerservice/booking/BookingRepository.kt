package com.ianarbuckle.gymplannerservice.booking

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface BookingRepository : CoroutineCrudRepository<Booking, String> {
    suspend fun findBookingsByPersonalTrainerId(id: String): Flow<Booking>

    suspend fun findBookingsByUserId(id: String): Flow<Booking>

    suspend fun findBookingsByBookingDate(bookingDate: LocalDate): Flow<Booking>
}
