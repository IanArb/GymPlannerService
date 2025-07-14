package com.ianarbuckle.gymplannerservice.messages.data

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MessagesRepository : CoroutineCrudRepository<Message, String>
