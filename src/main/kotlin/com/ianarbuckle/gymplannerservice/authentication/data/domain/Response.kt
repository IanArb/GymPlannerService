package com.ianarbuckle.gymplannerservice.authentication.data.domain

data class JwtResponse(
    val token: String,
)

data class MessageResponse(
    val message: String
)