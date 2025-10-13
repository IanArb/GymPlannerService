package com.ianarbuckle.gymplannerservice.booking.data

import com.ianarbuckle.gymplannerservice.availability.data.AvailabilityRepository
import com.ianarbuckle.gymplannerservice.availability.data.Status
import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.BookingsNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerAlreadyBookedException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileRepository
import com.ianarbuckle.gymplannerservice.utils.isEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    private val userProfileRepository: UserProfileRepository,
    private val availabilityRepository: AvailabilityRepository,
) : BookingService {

    override fun fetchAllBookings(): Flow<Booking> = bookingsRepository.findAll()

    override suspend fun fetchBookingById(id: String): Booking? = bookingsRepository.findById(id)

    override suspend fun findBookingsByPersonalTrainerId(id: String): Flow<Booking> {
        validatePersonalTrainer(id)
        val bookings = bookingsRepository.findBookingsByPersonalTrainerId(id)
        validateBookings(bookings)
        return bookings
    }

    override suspend fun findBookingsByUserId(id: String): Flow<Booking> {
        userProfileRepository.findByUserId(id) ?: throw UserNotFoundException()

        val bookings = bookingsRepository.findBookingsByUserId(id)
        validateBookings(bookings)
        return bookings
    }

    private suspend fun validateBookings(bookings: Flow<Booking>) {
        if (bookings.isEmpty()) {
            throw BookingsNotFoundException()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveBooking(booking: Booking): Booking {
        validatePersonalTrainer(booking.personalTrainer.id)

        bookingsRepository.findAll().collect {
            if (
                it.personalTrainer.id == booking.personalTrainer.id &&
                    it.bookingDate == booking.bookingDate &&
                    it.startTime == booking.startTime
            ) {
                throw PersonalTrainerAlreadyBookedException()
            }
        }

        val availability = availabilityRepository.findByTimeId(booking.timeSlotId)

        val updatedSlots = availability?.slots ?: throw AvailabilityNotFoundException()
        updatedSlots.map { slot ->
            val time = slot.times.find { it.id == booking.timeSlotId }

            if (time != null && time.status == Status.AVAILABLE) {
                val updatedTime = time.copy(status = Status.BOOKED)
                val updatedSlot = slot.copy(times = slot.times - time + updatedTime)
                availabilityRepository.save(
                    availability.copy(slots = availability.slots - slot + updatedSlot)
                )
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
