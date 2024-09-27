package com.ianarbuckle.gymplannerservice.clients.fakes

import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansService
import com.ianarbuckle.gymplannerservice.data.ClientsDataProvider
import com.ianarbuckle.gymplannerservice.clients.data.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeClientGymPlanService : ClientGymPlansService {
    override suspend fun findAllClients(): Flow<Client> = flowOf(ClientsDataProvider.createClient())

    override suspend fun findClientById(id: String): Client? = ClientsDataProvider.createClient()

    override suspend fun createClient(client: Client): Client = ClientsDataProvider.createClient()

    override suspend fun updateClient(client: Client) {
        // noop
    }

    override suspend fun deleteById(id: String) {
        // noop
    }

    override suspend fun deleteAll() {
        // noop
    }
}
