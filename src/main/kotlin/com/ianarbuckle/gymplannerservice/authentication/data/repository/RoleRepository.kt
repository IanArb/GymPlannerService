package com.ianarbuckle.gymplannerservice.authentication.data.repository

import com.ianarbuckle.gymplannerservice.authentication.data.model.ERole
import com.ianarbuckle.gymplannerservice.authentication.data.model.Role
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : CoroutineCrudRepository<Role, String> {
    suspend fun findByName(name: ERole): Role?
}