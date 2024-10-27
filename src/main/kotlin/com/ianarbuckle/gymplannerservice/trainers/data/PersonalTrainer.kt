package com.ianarbuckle.gymplannerservice.trainers.data

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class PersonalTrainer(
    @BsonId
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    val imageUrl: String,
    val bio: String,
    val socials: Map<String, String>? = null,
    val qualifications: List<String>,
    val gymLocation: GymLocation,
)

enum class GymLocation {
    CLONTARF,
    ASTONQUAY,
    LEOPARDSTOWN,
    DUNLOAGHAIRE,
    WESTMANSTOWN,
    SANDYMOUNT,
}
