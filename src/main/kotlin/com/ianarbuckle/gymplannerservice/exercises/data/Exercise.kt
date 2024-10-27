package com.ianarbuckle.gymplannerservice.exercises.data

import jakarta.validation.constraints.NotBlank
import org.bson.codecs.pojo.annotations.BsonId
import java.util.*

data class Exercise(
    @BsonId
    val id: String = UUID.randomUUID().toString(),
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,
    @field:NotBlank(message = "Description cannot be blank")
    val description: String,
    @field:NotBlank(message = "Image url cannot be blank")
    val imageUrl: String,
)