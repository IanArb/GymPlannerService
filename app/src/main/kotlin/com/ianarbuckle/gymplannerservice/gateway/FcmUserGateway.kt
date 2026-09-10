package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.authentication.User
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.fcm.data.UserGateway
import org.springframework.stereotype.Component

/**
 * Adapter wiring push-notifications' [UserGateway] port to the authentication
 * feature's repository. Lives in :app (the composition root) so neither
 * :push-notifications nor :authentication depends on the other.
 */
@Component
class FcmUserGateway(
    private val userRepository: UserRepository,
) : UserGateway {
    override suspend fun findById(userId: String): User? = userRepository.findById(userId)

    override suspend fun save(user: User): User = userRepository.save(user)
}
