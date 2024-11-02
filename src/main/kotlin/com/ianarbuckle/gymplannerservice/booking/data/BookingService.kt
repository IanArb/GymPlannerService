package com.ianarbuckle.gymplannerservice.booking.data

import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.utils.isEmpty
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface BookingService {
    fun fetchAllBookings(): Flow<Booking>
    suspend fun fetchBookingById(id: String): Booking?
    suspend fun findBookingsByPersonalTrainerId(id: String): Flow<Booking>
    suspend fun findBookingsByUserId(id: String): Flow<Booking>
    suspend fun saveBooking(booking: Booking): Booking
    suspend fun updateBooking(booking: Booking)
    suspend fun deleteBookingById(id: String)
}

@Service
class BookingServiceImpl(
    private val bookingsRepository: BookingRepository,
    private val personalTrainersRepository: PersonalTrainerRepository,
) : BookingService {

    override fun fetchAllBookings(): Flow<Booking> {
        return bookingsRepository.findAll()
    }

    override suspend fun fetchBookingById(id: String): Booking? {
        return bookingsRepository.findById(id)
    }

    override suspend fun findBookingsByPersonalTrainerId(id: String): Flow<Booking> {
        validatePersonalTrainer(id)
        val bookings = bookingsRepository.findBookingsByPersonalTrainerId(id)
        validateBookings(bookings)
        return bookings
    }

    override suspend fun findBookingsByUserId(id: String): Flow<Booking> {
        //TODO Validate user exists when authentication is implemented
        val bookings = bookingsRepository.findBookingsByClientUserId(id)
        validateBookings(bookings)
        return bookings
    }

    private suspend fun validateBookings(bookings: Flow<Booking>) {
        if (bookings.isEmpty()) {
            throw BookingsNotFoundException()
        }
    }

    override suspend fun saveBooking(booking: Booking): Booking {
        validatePersonalTrainer(booking.personalTrainer.id)

        bookingsRepository.findAll().collect {
            if (it.personalTrainer.id == booking.personalTrainer.id &&
                it.bookingDate.toLocalDate() == booking.bookingDate.toLocalDate() &&
                it.startTime == booking.startTime) {
                throw PersonalTrainerAlreadyBookedException()
            }
        }

        return bookingsRepository.save(booking.copy(status = BookingStatus.CONFIRMED))
    }

    private suspend fun validatePersonalTrainer(personalTrainerId: String) {
        personalTrainersRepository.findById(personalTrainerId)
            ?: throw PersonalTrainerNotFoundException()

    }

    override suspend fun updateBooking(booking: Booking) {
        bookingsRepository.save(booking).takeIf { bookingsRepository.existsById(booking.id ?: "") }
    }

    override suspend fun deleteBookingById(id: String) {
        bookingsRepository.deleteById(id)
    }
}