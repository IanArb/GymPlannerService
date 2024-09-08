package com.ianarbuckle.gymplannerservice.clients

import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansRepository
import com.ianarbuckle.gymplannerservice.model.Client
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface ClientGymPlansService {
    suspend fun findAllClients(): Flow<Client>

    suspend fun findClientById(id: String): Client?

    suspend fun createClient(client: Client): Client

    suspend fun updateClient(client: Client)

    suspend fun deleteById(id: String)

    suspend fun deleteAll()
}

@Service
class DefaultClientGymPlansService(
    private val repository: ClientGymPlansRepository,
) : ClientGymPlansService {
    override suspend fun findAllClients(): Flow<Client> = repository.findAll()

    override suspend fun findClientById(id: String): Client? = repository.findById(id)

    override suspend fun createClient(client: Client) = repository.save(client)

    override suspend fun deleteById(id: String) {
        repository.deleteById(id)
    }

    override suspend fun updateClient(client: Client) {
        repository.save(client).takeIf { repository.existsById(client.id ?: "") }
    }

    override suspend fun deleteAll() {
        repository.deleteAll()
    }
}
