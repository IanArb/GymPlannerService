package com.ianarbuckle.gymplannerservice.userProfile.data

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository : CoroutineCrudRepository<UserProfile, String> {
    suspend fun findByUserId(id: String): UserProfile?

    suspend fun existsByUserId(id: String): Boolean
}
