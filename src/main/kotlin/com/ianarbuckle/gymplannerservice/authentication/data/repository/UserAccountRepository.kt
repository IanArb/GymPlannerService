package com.ianarbuckle.gymplannerservice.authentication.data.repository

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserAccount
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepository : CoroutineCrudRepository<UserAccount, String>