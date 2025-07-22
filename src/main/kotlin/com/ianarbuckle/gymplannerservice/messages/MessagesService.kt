package com.ianarbuckle.gymplannerservice.messages

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import com.ianarbuckle.gymplannerservice.messages.data.Message
import com.ianarbuckle.gymplannerservice.messages.data.MessagesRepository

interface MessagesService {
    fun findAlMessages(): Flow<Message>

    suspend fun insertMessage(message: Message)
}

@Service
class MessagesServiceImpl(
    private val repository: MessagesRepository,
) : MessagesService {
    override fun findAlMessages(): Flow<Message> = repository.findAll()

    override suspend fun insertMessage(message: Message) {
        repository.save(message)
    }
}
