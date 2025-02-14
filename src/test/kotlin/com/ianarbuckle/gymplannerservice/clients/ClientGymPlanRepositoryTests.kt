package com.ianarbuckle.gymplannerservice.clients

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.clients.fakes.FakeClientGymPlanRepository
import com.ianarbuckle.gymplannerservice.mocks.ClientsDataProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientGymPlanRepositoryTests {
    private val gymPlanRepository = FakeClientGymPlanRepository()

    @Test
    fun `verify that find returns the list of clients`() =
        runTest {
            gymPlanRepository.findAll().test {
                assertThat(awaitItem()).isEqualTo(ClientsDataProvider.createClient())
                assertThat(awaitItem()).isEqualTo(ClientsDataProvider.createClient(id = "2"))
                awaitComplete()
            }
        }

    @Test
    fun `verify save creates the client`() =
        runTest {
            assertThat(gymPlanRepository.save(ClientsDataProvider.createClient()))
                .isEqualTo(ClientsDataProvider.createClient())
        }
}
