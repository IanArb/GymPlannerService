package com.ianarbuckle.gymplannerservice.fitnessclass.data

import FutureDate
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.DayOfWeek
import java.time.LocalDateTime

@Document
data class FitnessClass(
    @BsonId
    val id: String? = null,
    val dayOfWeek: DayOfWeek,
    @field:FutureDate
    @field:NotNull(message = "Time is mandatory")
    val startTime: LocalDateTime,
    @field:NotNull(message = "Duration is mandatory")
    val duration: Duration,
    @field:NotEmpty(message = "Name is mandatory")
    val name: String,
    @field:NotEmpty(message = "Description is mandatory")
    val description: String,
    @field:NotEmpty(message = "Image is mandatory")
    val imageUrl: String,
)

data class Duration(
    val value: Long,
    val unit: String
)