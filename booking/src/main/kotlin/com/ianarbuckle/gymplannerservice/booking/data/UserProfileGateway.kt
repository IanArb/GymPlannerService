package com.ianarbuckle.gymplannerservice.booking.data

/**
 * Port for the minimal user-profile check booking needs (verifying a profile
 * exists for a user id). Implemented by the user-profile feature so that
 * :booking does not depend on it (dependency inversion — breaks the
 * booking ↔ userProfile cycle).
 */
interface UserProfileGateway {
    suspend fun existsByUserId(userId: String): Boolean
}
