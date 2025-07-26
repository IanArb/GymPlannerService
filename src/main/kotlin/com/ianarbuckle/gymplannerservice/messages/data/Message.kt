package com.ianarbuckle.gymplannerservice.messages.data

import jakarta.validation.constraints.Future
import java.time.LocalDateTime
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Message(
    @BsonId val id: String = ObjectId.get().toHexString(),
    val userId: String,
    val username: String,
    val content: String,
    @field:Future val timestamp: LocalDateTime,
)
