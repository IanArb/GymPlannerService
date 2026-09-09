package com.ianarbuckle.gymplannerservice.userProfile.data

import com.ianarbuckle.gymplannerservice.booking.UserProfileGateway
import org.springframework.stereotype.Component

/**
 * Implements booking's [UserProfileGateway] port using the user-profile feature's
 * repository, so :booking can verify a profile exists without depending on this
 * feature.
 */
@Component
class BookingUserProfileGateway(
    private val userProfileRepository: UserProfileRepository,
) : UserProfileGateway {
    override suspend fun existsByUserId(userId: String): Boolean = userProfileRepository.existsByUserId(userId)
}
