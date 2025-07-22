package com.ianarbuckle.gymplannerservice.clients.data

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository interface ClientGymPlansRepository : CoroutineCrudRepository<Client, String>
