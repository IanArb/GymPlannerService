package com.ianarbuckle.gymplannerservice.booking.data

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : CoroutineCrudRepository<Booking, String> {
    suspend fun findBookingsByPersonalTrainerId(id: String): Flow<Booking>

    suspend fun findBookingsByUserId(id: String): Flow<Booking>
}
