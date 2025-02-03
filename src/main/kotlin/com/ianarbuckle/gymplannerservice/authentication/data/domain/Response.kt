package com.ianarbuckle.gymplannerservice.authentication.data.domain

data class JwtResponse(
    val userId: String,
    val token: String,
    val expiration: Long,
)

data class MessageResponse(
    val message: String,
)
