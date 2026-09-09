package com.ianarbuckle.gymplannerservice.authentication.data.service

import com.ianarbuckle.gymplannerservice.authentication.UserProfile

/**
 * Port for persisting the profile created during registration. Implemented by
 * the user-profile feature so that `:authentication` does not depend on that
 * feature's repository (dependency inversion — breaks the auth ↔ userProfile
 * cycle).
 */
interface UserProfileRegistrar {
    suspend fun save(userProfile: UserProfile)
}
