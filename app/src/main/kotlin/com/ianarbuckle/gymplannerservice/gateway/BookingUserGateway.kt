package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.booking.UserGateway
import org.springframework.stereotype.Component

/**
 * Adapter wiring booking's [UserGateway] port to the authentication feature's
 * repository.
 */
@Component
class BookingUserGateway(
    private val userRepository: UserRepository,
) : UserGateway {
    override suspend fun findPushNotificationToken(userId: String): String? = userRepository.findById(userId)?.pushNotificationToken
}
