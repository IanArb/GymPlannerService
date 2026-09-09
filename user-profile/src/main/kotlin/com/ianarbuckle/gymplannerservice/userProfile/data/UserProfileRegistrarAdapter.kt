package com.ianarbuckle.gymplannerservice.userProfile.data

import com.ianarbuckle.gymplannerservice.authentication.UserProfile
import com.ianarbuckle.gymplannerservice.authentication.data.service.UserProfileRegistrar
import org.springframework.stereotype.Component

/**
 * Implements the authentication layer's [UserProfileRegistrar] port using the
 * user-profile feature's repository, so `:authentication` can create a profile
 * on registration without depending on this feature.
 */
@Component
class UserProfileRegistrarAdapter(
    private val userProfileRepository: UserProfileRepository,
) : UserProfileRegistrar {
    override suspend fun save(userProfile: UserProfile) {
        userProfileRepository.save(userProfile)
    }
}
