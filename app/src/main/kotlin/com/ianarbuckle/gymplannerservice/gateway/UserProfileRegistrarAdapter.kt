package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.authentication.UserProfile
import com.ianarbuckle.gymplannerservice.authentication.data.service.UserProfileRegistrar
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileRepository
import org.springframework.stereotype.Component

/**
 * Adapter wiring the authentication layer's [UserProfileRegistrar] port to the
 * user-profile feature's repository.
 */
@Component
class UserProfileRegistrarAdapter(
    private val userProfileRepository: UserProfileRepository,
) : UserProfileRegistrar {
    override suspend fun save(userProfile: UserProfile) {
        userProfileRepository.save(userProfile)
    }
}
