package com.ianarbuckle.gymplannerservice.gymlocations.data

import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class GymLocation(
    @BsonId
    val id: String? = null,
    val title: String,
    val subTitle: String,
    val description: String,
    val imageUrl: String,
)