package com.ianarbuckle.gymplannerservice.model

import FutureDate
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "ClientGymPlan")
data class Client(
    @BsonId
    val id: String? = null,
    @field:NotBlank(message = "First name is mandatory")
    val firstName: String,
    @field:NotBlank(message = "Last name is mandatory")
    val surname: String,
    @field:NotBlank(message = "Strength level is mandatory")
    @field:NotNull
    val strengthLevel: String,
    @field:Valid
    val gymPlan: GymPlan,
)

@Document
data class GymPlan(
    @field:NotBlank(message = "Name is mandatory")
    val name: String,
    @field:Valid
    val personalTrainer: PersonalTrainer,
    @field:FutureDate
    @field:NotNull(message = "Start date is mandatory")
    val startDate: LocalDateTime,
    @field:FutureDate
    @field:NotNull(message = "End date is mandatory")
    val endDate: LocalDateTime,
    val sessions: List<Session>,
)

@Document
data class PersonalTrainer(
    @BsonId
    val id: String? = null,
    @field:Size(min = 2, message = "First name minimum 2 characters allowed")
    val firstName: String,
    @field:Size(min = 2, message = "Last name minimum 2 characters allowed")
    val surname: String,
    val socials: Map<String, String>? = emptyMap(),
)

@Document
data class Session(
    val name: String,
    val workouts: List<Workout>,
)

@Document
data class Workout(
    val name: String,
    val sets: Int,
    val repetitions: Int,
    val weight: Weight,
    val note: String? = "",
)

@Document
data class Weight(
    val value: Double,
    val unit: String,
)
