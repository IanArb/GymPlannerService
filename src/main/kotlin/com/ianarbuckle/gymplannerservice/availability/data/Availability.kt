package com.ianarbuckle.gymplannerservice.availability.data

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Schema(description = "Availability information")
@Document
data class Availability(
    @BsonId val id: String? = null,
    @Schema(description = "Personal trainer id") val personalTrainerId: String,
    @Schema(description = "Month") val month: String,
    @Schema(description = "Appointment Slots") val slots: List<AppointmentSlots>,
)

@Schema(description = "Appointment Slots")
@Document
data class AppointmentSlots(
    val id: String = ObjectId().toHexString(),
    val date: LocalDate,
    val times: List<Time>,
)

@Schema(description = "Time information")
@Document
data class Time(
    val id: String = ObjectId().toHexString(),
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: Status,
)

enum class Status {
    AVAILABLE,
    UNAVAILABLE,
    BOOKED,
}

@Schema(description = "Check availability information")
@Document
data class CheckAvailability(
    val personalTrainerId: String,
    val isAvailable: Boolean,
)
