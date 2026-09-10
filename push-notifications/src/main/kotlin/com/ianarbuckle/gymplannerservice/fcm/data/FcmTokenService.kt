package com.ianarbuckle.gymplannerservice.fcm.data

import org.springframework.stereotype.Service

interface FcmTokenService {
    suspend fun registerToken(
        userId: String,
        token: String,
    ): FcmTokenResponse

    suspend fun deleteToken(userId: String)
}

@Service
class FcmTokenServiceImpl(
    private val userGateway: UserGateway,
) : FcmTokenService {
    override suspend fun registerToken(
        userId: String,
        token: String,
    ): FcmTokenResponse {
        val user = userGateway.findById(userId)
        val existingToken = user?.pushNotificationToken
        if (existingToken == null && user != null) {
            val updatedUserWithToken = user.copy(pushNotificationToken = token)
            val user = userGateway.save(updatedUserWithToken)
            return FcmTokenResponse(token = user.pushNotificationToken)
        }
        return FcmTokenResponse(token = existingToken)
    }

    override suspend fun deleteToken(userId: String) {
        val user = userGateway.findById(userId)
        val token = user?.pushNotificationToken
        if (token != null) {
            val updatedUserWithToken = user.copy(pushNotificationToken = null)
            userGateway.save(updatedUserWithToken)
        }
    }
}
