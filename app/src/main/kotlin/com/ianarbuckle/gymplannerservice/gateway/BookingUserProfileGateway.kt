package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.booking.UserProfileGateway
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileRepository
import org.springframework.stereotype.Component

/**
 * Adapter wiring booking's [UserProfileGateway] port to the user-profile feature's
 * repository.
 */
@Component
class BookingUserProfileGateway(
    private val userProfileRepository: UserProfileRepository,
) : UserProfileGateway {
    override suspend fun existsByUserId(userId: String): Boolean = userProfileRepository.existsByUserId(userId)
}
