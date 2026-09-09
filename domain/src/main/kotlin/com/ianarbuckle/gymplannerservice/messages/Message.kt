package com.ianarbuckle.gymplannerservice.messages

import jakarta.validation.constraints.Future
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Message(
    @BsonId val id: String = ObjectId.get().toHexString(),
    val userId: String,
    val username: String,
    val content: String,
    @field:Future val timestamp: Instant,
)
