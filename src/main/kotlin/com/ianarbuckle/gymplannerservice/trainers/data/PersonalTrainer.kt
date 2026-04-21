package com.ianarbuckle.gymplannerservice.trainers.data

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.ianarbuckle.gymplannerservice.common.GymLocation
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.Locale
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class PersonalTrainer(
    @BsonId val id: String? = null,
    val firstName: String,
    val lastName: String,
    val imageUrl: String,
    val bio: String,
    val socials: Map<String, String>? = null,
    val qualifications: List<String>,
    val gymLocation: GymLocation,
    val schedule: List<ScheduledShift> = emptyList(),
    val availabilityStatus: TrainerAvailabilityStatus = TrainerAvailabilityStatus.UNAVAILABLE,
)

enum class TrainerAvailabilityStatus {
    AVAILABLE,
    UNAVAILABLE,
}

data class ScheduledShift(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

class GymLocationDeserializer : JsonDeserializer<GymLocation>() {
    override fun deserialize(
        parser: JsonParser,
        ctxt: DeserializationContext,
    ): GymLocation {
        val value = parser.text.uppercase(Locale.getDefault())
        return GymLocation.valueOf(value)
    }
}
