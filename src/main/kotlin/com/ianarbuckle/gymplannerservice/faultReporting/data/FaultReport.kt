package com.ianarbuckle.gymplannerservice.faultReporting.data

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.bson.codecs.pojo.annotations.BsonId
import java.time.LocalDateTime

data class FaultReport(
    @BsonId
    val id: String? = null,
    @field:Positive
    val machineNumber: Int,
    @field:NotEmpty(message = "Description is mandatory")
    val description: String,
    @field:NotEmpty(message = "Photo is mandatory")
    val photoUri: String,
    @field:NotNull(message = "Date is mandatory")
    val date: LocalDateTime
)