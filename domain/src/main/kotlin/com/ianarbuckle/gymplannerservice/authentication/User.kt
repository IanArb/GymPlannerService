package com.ianarbuckle.gymplannerservice.authentication

import com.ianarbuckle.gymplannerservice.common.GymLocation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.bson.codecs.pojo.annotations.BsonId
import org.springframework.data.mongodb.core.mapping.Document

@Schema(description = "User information")
@Document
data class User(
    @BsonId val id: String,
    @NotBlank @Size(max = 20) val username: String,
    @NotBlank @Size(max = 120) val password: String?,
    @NotBlank @Size(max = 50) val email: String,
    val roles: Set<ERole> = emptySet(),
    val pushNotificationToken: String? = null,
)

enum class ERole {
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN,
}

@Schema(description = "User profile information")
@Document
data class UserProfile(
    @BsonId val userId: String,
    val username: String,
    val firstName: String,
    val surname: String,
    val email: String,
    val gymLocation: GymLocation? = null,
)
