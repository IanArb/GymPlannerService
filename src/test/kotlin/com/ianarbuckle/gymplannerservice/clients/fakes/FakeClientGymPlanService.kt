package com.ianarbuckle.gymplannerservice.clients.fakes

import com.ianarbuckle.gymplannerservice.clients.ClientGymPlansService
import com.ianarbuckle.gymplannerservice.data.DataProvider
import com.ianarbuckle.gymplannerservice.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeClientGymPlanService : ClientGymPlansService {

    override suspend fun findAllClients(): Flow<Client> {
        return flowOf(DataProvider.createClient())
    }

    override suspend fun findClientById(id: String): Client? {
        return DataProvider.createClient()
    }

    override suspend fun createClient(client: Client): Client {
        return DataProvider.createClient()
    }

    override suspend fun updateClient(client: Client) {
        //noop
    }

    override suspend fun deleteById(id: String) {
        //noop
    }

    override suspend fun deleteAll() {
        //noop
    }
}