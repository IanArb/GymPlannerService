package com.ianarbuckle.gymplannerservice.clients.fakes

import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansRepository
import com.ianarbuckle.gymplannerservice.data.DataProvider
import com.ianarbuckle.gymplannerservice.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

class FakeClientGymPlanRepository : ClientGymPlansRepository {

    override suspend fun count(): Long {
        return 2L
    }

    override suspend fun delete(entity: Client) {
        //noop
    }

    override suspend fun deleteAll() {
        //noop
    }

    override suspend fun deleteAll(entities: Iterable<Client>) {
        //noop
    }

    override suspend fun <S : Client> deleteAll(entityStream: Flow<S>) {
        //noop
    }

    override suspend fun deleteAllById(ids: Iterable<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun existsById(id: String): Boolean {
        return true
    }

    override fun findAll(): Flow<Client> {
        return flowOf(
            DataProvider.createClient(),
            DataProvider.createClient(
                id = "2",
            )
        )
    }

    override fun findAllById(ids: Iterable<String>): Flow<Client> {
        return flowOf(
            DataProvider.createClient(),
            DataProvider.createClient(
                id = Random.nextLong().toString(),
            )
        )
    }

    override fun findAllById(ids: Flow<String>): Flow<Client> {
        return flowOf(
            DataProvider.createClient(),
            DataProvider.createClient(
                id = Random.nextLong().toString(),
            )
        )
    }

    override suspend fun findById(id: String): Client? {
        TODO("Not yet implemented")
    }

    override suspend fun <S : Client> save(entity: S): Client {
        return DataProvider.createClient()
    }

    override fun <S : Client> saveAll(entities: Iterable<S>): Flow<S> {
        TODO("Not yet implemented")
    }

    override fun <S : Client> saveAll(entityStream: Flow<S>): Flow<S> {
        TODO("Not yet implemented")
    }
}