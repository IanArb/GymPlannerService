package com.ianarbuckle.gymplannerservice.fcm.data

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import org.springframework.stereotype.Service

interface FcmTokenService {
    suspend fun registerToken(userId: String, token: String): FcmTokenResponse

    suspend fun deleteToken(userId: String)
}

@Service
class FcmTokenServiceImpl(
    private val userRepository: UserRepository,
) : FcmTokenService {

    override suspend fun registerToken(userId: String, token: String): FcmTokenResponse {
        val user = userRepository.findById(userId)
        val existingToken = user?.pushNotificationToken
        if (existingToken == null && user != null) {
            val updatedUserWithToken = user.copy(pushNotificationToken = token)
            val user = userRepository.save(updatedUserWithToken)
            return FcmTokenResponse(token = user.pushNotificationToken)
        }
        return FcmTokenResponse(token = existingToken)
    }

    override suspend fun deleteToken(userId: String) {
        val user = userRepository.findById(userId)
        val token = user?.pushNotificationToken
        if (token != null) {
            val updatedUserWithToken = user.copy(pushNotificationToken = null)
            userRepository.save(updatedUserWithToken)
        }
    }
}
