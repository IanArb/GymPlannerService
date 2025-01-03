package com.ianarbuckle.gymplannerservice.availability.data

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalTime


data class Availability(
    @BsonId val id: String? = null,
    val personalTrainerId: String,
    val month: String,
    val slots: List<AppointmentSlots>,
)

data class AppointmentSlots(
    val id: String = ObjectId().toHexString(),
    val date: LocalDate,
    val times: List<Time>,
)

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

data class CheckAvailability(
    val personalTrainerId: String,
    val isAvailable: Boolean,
)