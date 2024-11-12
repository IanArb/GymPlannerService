package com.ianarbuckle.gymplannerservice.userProfile.data

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import org.springframework.stereotype.Service

interface UserProfileService {
    suspend fun userProfile(id: String): UserProfile?
    suspend fun deleteUserProfile(id: String)
    suspend fun updateUserProfile(userProfile: UserProfile)
}

@Service
class UserProfileServiceImpl(private val userProfileRepository: UserProfileRepository) : UserProfileService {

    override suspend fun userProfile(id: String): UserProfile? {
        return userProfileRepository.findByUserId(id) ?: throw UserNotFoundException()
    }

    override suspend fun deleteUserProfile(id: String) {
        return userProfileRepository.deleteById(id)
    }

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        if (userProfileRepository.existsByUserId(userProfile.userId)) {
            userProfileRepository.save(userProfile)
        } else {
            throw UserNotFoundException()
        }
    }
}