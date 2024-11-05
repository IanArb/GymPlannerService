package com.ianarbuckle.gymplannerservice.fitnessclass.fakes

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.mocks.FitnessClassDataProvider
import com.ianarbuckle.gymplannerservice.fitnessclass.FakeFitnessClassRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.TestInstance
import java.time.DayOfWeek
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FitnessClassRepositoryTests {

    private val repository = FakeFitnessClassRepository()

    @Test
    fun `verify that find returns classes by MONDAY`() =
        runTest {
            repository.findFitnessClassesByDayOfWeek(DayOfWeek.MONDAY).test {
                assertThat(awaitItem()).isEqualTo(FitnessClassDataProvider.createClass())
                awaitComplete()
            }
        }

    @Test
    fun `verify save creates the client`() {
        val entity = FitnessClassDataProvider.createClass()
        runTest {
            assertThat(repository.save(entity)).isEqualTo(entity)
        }
    }
}