package com.ianarbuckle.gymplannerservice.messages

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface MessagesService {
    fun findAlMessages(): Flow<Message>

    suspend fun insertMessage(message: Message): Message
}

@Service
class MessagesServiceImpl(
    private val repository: MessagesRepository,
) : MessagesService {
    override fun findAlMessages(): Flow<Message> = repository.findAll()

    override suspend fun insertMessage(message: Message): Message = repository.save(message)
}
