package com.ianarbuckle.gymplannerservice.booking.data

import FutureDate
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.time.LocalTime

@Schema(description = "Booking information")
@Document
data class Booking(
    @BsonId
    val id: String? = null,
    @Schema(description = "Client information")
    @field:NotNull(message = "Client information is mandatory")
    val client: Client,
    @Schema(description = "Booking date")
    @field:NotNull(message = "Booking date is mandatory")
    @field:FutureDate
    val bookingDate: LocalDateTime,
    @Schema(description = "Start time")
    @field:NotNull(message = "Start time is mandatory")
    val startTime: LocalTime,
    @Schema(description = "Personal trainer information")
    @field:NotNull(message = "Personal trainer is mandatory")
    val personalTrainer: PersonalTrainerBooking,
    @Schema(description = "Booking status")
    val status: BookingStatus? = BookingStatus.PENDING,
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
}

@Schema(description = "Client information")
@Document
data class Client(
    val userId: String,
    @Schema(description = "First name")
    @field:NotBlank(message = "First name is mandatory")
    val firstName: String,
    @Schema(description = "Surname")
    @field:NotBlank(message = "Surname is mandatory")
    val surname: String,
    @Schema(description = "Email")
    @field:NotBlank(message = "Email is mandatory")
    val email: String,
    @Schema(description = "Gym Location")
    val gymLocation: GymLocation,
)

@Schema(description = "Personal trainer information")
@Document
data class PersonalTrainerBooking(
    val id: String,
    @Schema(description = "First name")
    val firstName: String,
    @Schema(description = "Surname")
    val surname: String,
    @Schema(description = "Image URL")
    val imageUrl: String,
    @Schema(description = "Gym location")
    val gymLocation: GymLocation,
)
