package com.ianarbuckle.gymplannerservice.authentication.data.model

import com.ianarbuckle.gymplannerservice.booking.data.Booking
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @BsonId
    val id: String? = null,
    @NotBlank
    @Size(max = 20)
    val username: String,
    @NotBlank
    @Size(max = 120)
    val password: String,
    @NotBlank
    @Size(max = 50)
    val email: String,
    @DBRef
    val roles: Set<Role> = emptySet(),
)

@Document(collection = "roles")
data class Role(
    @BsonId
    val id: String? = null,
    val name: ERole,
)

enum class ERole {
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN
}

@Document
data class UserAccount(
    val id: String,
    val username: String,
    val firstName: String,
    val surname: String,
    val bookings: List<Booking> = emptyList(),
)
