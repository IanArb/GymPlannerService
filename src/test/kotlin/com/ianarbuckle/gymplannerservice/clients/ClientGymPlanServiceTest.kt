package com.ianarbuckle.gymplannerservice.clients

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.clients.data.ClientGymPlansRepository
import com.ianarbuckle.gymplannerservice.data.DataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientGymPlanServiceTest {
    private val mockClientGymPlanRepository = mockk<ClientGymPlansRepository>()

    private val service: ClientGymPlansService = DefaultClientGymPlansService(mockClientGymPlanRepository)

    @Test
    fun `verify when the service finds all, the repository will return all clients`() =
        runTest {
            coEvery { mockClientGymPlanRepository.findAll() } returns flowOf(DataProvider.createClient())

            service.findAllClients().test {
                assertThat(awaitItem()).isEqualTo(DataProvider.createClient())
                awaitComplete()
            }
        }

    @Test
    fun `verify when the service finds a client by its id, the repository will return the client by its id`() =
        runTest {
            coEvery { mockClientGymPlanRepository.findById(any()) } returns DataProvider.createClient()

            assertThat(service.findClientById("123456789")).isEqualTo(
                DataProvider.createClient(
                    id = "123456789",
                ),
            )
        }

    @Test
    fun `verify when the service saves a client, the repository will save the client`() =
        runTest {
            coEvery { mockClientGymPlanRepository.save(any()) } returns DataProvider.createClient()

            assertThat(service.createClient(DataProvider.createClient())).isEqualTo(DataProvider.createClient())
        }

    @Test
    fun `verify when the service updates a client, the repository will save the client`() =
        runTest {
            coEvery { mockClientGymPlanRepository.existsById(any()) } returns true
            coEvery { mockClientGymPlanRepository.save(any()) } returns DataProvider.createClient()

            service.updateClient(DataProvider.createClient())

            coVerify { service.updateClient(DataProvider.createClient()) }
        }

    @Test
    fun `verify when the service deletes a client by id, the repository will delete the client by its id`() =
        runTest {
            coEvery { mockClientGymPlanRepository.deleteById(any()) } returns Unit

            service.deleteById("1")

            coVerify { service.deleteById("1") }
        }

    @Test
    fun `verify when the service deletes all clients, the repository will delete all clients`() =
        runTest {
            coEvery { mockClientGymPlanRepository.deleteAll() } returns Unit

            service.deleteAll()

            coVerify { service.deleteAll() }
        }
}
