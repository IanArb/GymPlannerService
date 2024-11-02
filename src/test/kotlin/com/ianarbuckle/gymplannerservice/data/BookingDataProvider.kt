package com.ianarbuckle.gymplannerservice.data

import com.ianarbuckle.gymplannerservice.booking.data.Booking
import com.ianarbuckle.gymplannerservice.booking.data.BookingStatus
import com.ianarbuckle.gymplannerservice.booking.data.Client
import com.ianarbuckle.gymplannerservice.booking.data.PersonalTrainerBooking
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import java.time.LocalDateTime
import java.time.LocalTime

object BookingDataProvider {

    fun createBooking(
        id: String = "1",
        bookingDate: LocalDateTime = LocalDateTime.of(2021, 10, 10, 10, 0),
        startTime: LocalTime = LocalTime.of(10, 0),
        client: Client = createClient(),
        personalTrainer: PersonalTrainerBooking = createPersonalTrainer(),
        status: BookingStatus = BookingStatus.PENDING,
    ): Booking {
        return Booking(
            id = id,
            bookingDate = bookingDate,
            startTime = startTime,
            client = client,
            personalTrainer = personalTrainer,
            status = status,
        )
    }

    private fun createClient(
        userId: String = "1",
        firstName: String = "John",
        surname: String = "Doe",
        email: String = "john.doe@mail.com",
        gymLocation: GymLocation = GymLocation.CLONTARF,
    ): Client {
        return Client(
            userId = userId,
            firstName = firstName,
            surname = surname,
            email = email,
            gymLocation = gymLocation,
        )
    }

    private fun createPersonalTrainer(
        id: String = "1",
        firstName: String = "John",
        lastName: String = "Doe",
        imageUrl: String = "https://example.com",
        gymLocation: GymLocation = GymLocation.CLONTARF
    ): PersonalTrainerBooking {
        return PersonalTrainerBooking(
            id = id,
            firstName = firstName,
            surname = lastName,
            imageUrl = imageUrl,
            gymLocation = gymLocation,
        )
    }
}