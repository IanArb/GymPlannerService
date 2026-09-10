package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.fitnessclass.UserGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.springframework.stereotype.Component

/**
 * Adapter wiring fitness-class's [UserGateway] port to the authentication feature's
 * repository. Lives in :app (the composition root) so neither :fitness-class nor
 * :authentication depends on the other.
 */
@Component
class ClassesUserGateway(
    private val userRepository: UserRepository,
) : UserGateway {
    override fun findAllPushNotificationTokens(): Flow<String> =
        userRepository.findAll().mapNotNull { user ->
            user.pushNotificationToken?.takeIf { it.isNotEmpty() }
        }
}
