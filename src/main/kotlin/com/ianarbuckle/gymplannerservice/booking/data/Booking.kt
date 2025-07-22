package com.ianarbuckle.gymplannerservice.booking.data

import FutureDate
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Schema(description = "Booking information")
@Document
data class Booking(
    @BsonId val id: String? = null,
    @Schema(description = "Time slot id") val timeSlotId: String,
    @field:NotNull(message = "User id is mandatory") val userId: String,
    @Schema(description = "Booking date")
    @field:NotNull(message = "Booking date is mandatory")
    @field:FutureDate
    val bookingDate: LocalDate,
    @Schema(description = "Start time")
    @field:NotNull(message = "Start time is mandatory")
    val startTime: LocalTime,
    @Schema(description = "Personal trainer information")
    @field:NotNull(message = "Personal trainer is mandatory")
    val personalTrainer: PersonalTrainerBooking,
    @Schema(description = "Booking status") val status: BookingStatus? = BookingStatus.PENDING,
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
}

@Schema(description = "Personal trainer information")
@Document
data class PersonalTrainerBooking(
    val id: String,
    @Schema(description = "Name") val name: String,
    @Schema(description = "Image URL") val imageUrl: String,
    @Schema(description = "Gym location") val gymLocation: GymLocation,
)
