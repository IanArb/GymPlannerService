package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.model.Client
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientGymPlansRepository : CoroutineCrudRepository<Client, String>