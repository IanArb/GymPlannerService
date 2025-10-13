package com.ianarbuckle.gymplannerservice.fcm.data

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import org.springframework.stereotype.Service

interface FcmTokenService {
    suspend fun registerToken(userId: String, token: String)

    suspend fun deleteToken(userId: String)
}

@Service
class FcmTokenServiceImpl(
    private val userRepository: UserRepository,
) : FcmTokenService {

    override suspend fun registerToken(userId: String, token: String) {
        val user = userRepository.findById(userId)
        val existingToken = user?.pushNotificationToken
        if (existingToken == null && user != null) {
            val updatedUserWithToken = user.copy(pushNotificationToken = token)
            userRepository.save(updatedUserWithToken)
        }
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
