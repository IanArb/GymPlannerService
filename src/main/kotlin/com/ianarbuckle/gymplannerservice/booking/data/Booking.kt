package com.ianarbuckle.gymplannerservice.booking.data

import FutureDate
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.LocalTime

@Document
data class Booking(
    @BsonId
    val id: String? = null,
    @field:NotNull(message = "Client information is mandatory")
    val client: Client,
    @field:NotNull(message = "Booking date is mandatory")
    @field:FutureDate
    val bookingDate: LocalDateTime,
    @field:NotNull(message = "Start time is mandatory")
    val startTime: LocalTime,
    @field:NotNull(message = "Personal trainer is mandatory")
    val personalTrainer: PersonalTrainerBooking,
    val status: BookingStatus? = BookingStatus.PENDING,
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

@Document
data class Client(
    val userId: String,
    @field:NotBlank(message = "First name is mandatory")
    val firstName: String,
    @field:NotBlank(message = "Surname is mandatory")
    val surname: String,
    @field:NotBlank(message = "Email is mandatory")
    val email: String,
    val gymLocation: GymLocation
)

@Document
data class PersonalTrainerBooking(
    val id: String,
    val firstName: String,
    val surname: String,
    val imageUrl: String,
    val gymLocation: GymLocation
)
