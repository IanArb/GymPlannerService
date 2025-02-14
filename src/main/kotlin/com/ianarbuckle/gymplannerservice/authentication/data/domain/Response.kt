package com.ianarbuckle.gymplannerservice.authentication.data.domain

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Jwt response")
data class JwtResponse(
    val userId: String,
    val token: String,
    val expiration: Long,
)

@Schema(description = "Message response")
data class MessageResponse(
    val message: String,
)
