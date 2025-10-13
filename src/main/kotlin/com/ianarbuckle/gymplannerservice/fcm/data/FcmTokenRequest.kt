package com.ianarbuckle.gymplannerservice.fcm.data

data class FcmTokenRequest(
    val userId: String,
    val token: String,
)
