package com.ianarbuckle.gymplannerservice.checkin.data

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class CheckIn(
    @BsonId val id: String? = null,
    @Schema(description = "Personal trainer ID") val trainerId: String,
    @Schema(description = "Time the trainer checked in") val checkInTime: LocalDateTime,
    @Schema(description = "Time the trainer checked out") val checkOutTime: LocalDateTime? = null,
    @Schema(description = "Check-in status") val status: CheckInStatus,
)

enum class CheckInStatus {
    ON_TIME,
    LATE,
}

@Schema(description = "Check-in request body")
data class CheckInRequest(
    @Schema(description = "Time of check-in") val checkInTime: LocalDateTime,
)

@Schema(description = "Check-out request body")
data class CheckOutRequest(
    @Schema(description = "Time of check-out") val checkOutTime: LocalDateTime,
)
