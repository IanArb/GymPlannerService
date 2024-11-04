package com.ianarbuckle.gymplannerservice.authentication.data.repository

import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByUsername(username: String): User?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
}