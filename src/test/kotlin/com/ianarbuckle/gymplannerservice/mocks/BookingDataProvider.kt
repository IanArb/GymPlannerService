package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.booking.data.Booking
import com.ianarbuckle.gymplannerservice.booking.data.BookingStatus
import com.ianarbuckle.gymplannerservice.booking.data.PersonalTrainerBooking
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import java.time.LocalDate
import java.time.LocalTime

object BookingDataProvider {
    fun createBooking(
        id: String = "1",
        timeSlotId: String = "1",
        userId: String = "1",
        bookingDate: LocalDate = LocalDate.of(2025, 5, 10),
        startTime: LocalTime = LocalTime.of(10, 0),
        personalTrainer: PersonalTrainerBooking = createPersonalTrainer(),
        status: BookingStatus = BookingStatus.PENDING,
    ): Booking =
        Booking(
            id = id,
            timeSlotId = timeSlotId,
            bookingDate = bookingDate,
            startTime = startTime,
            userId = userId,
            personalTrainer = personalTrainer,
            status = status,
        )

    private fun createPersonalTrainer(
        id: String = "1",
        name: String = "John Doe",
        imageUrl: String = "https://example.com",
        gymLocation: GymLocation = GymLocation.CLONTARF,
    ): PersonalTrainerBooking =
        PersonalTrainerBooking(
            id = id,
            name = name,
            imageUrl = imageUrl,
            gymLocation = gymLocation,
        )
}
