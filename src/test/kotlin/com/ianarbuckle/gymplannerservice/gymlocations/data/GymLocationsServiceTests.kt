package com.ianarbuckle.gymplannerservice.gymlocations.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.mocks.GymLocationsProvider
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GymLocationsServiceTests {
    private val gymLocationsRepository: GymLocationsRepository = mockk()

    private val gymLocationsService: GymLocationsService = GymLocationsServiceImpl(gymLocationsRepository)

    @Test
    fun `should return all gym locations`() =
        runTest {
            // Given
            val gymLocations = GymLocationsProvider.gymLocations()

            every { gymLocationsRepository.findAll() } returns gymLocations

            // When
            gymLocationsService.findAllGymLocations().test {
                assertThat(awaitItem()).isEqualTo(gymLocations.first())
                assertThat(awaitItem()).isEqualTo(gymLocations.last())
                awaitComplete()
            }

            // Then
            verify { gymLocationsRepository.findAll() }
        }

    @Test
    fun `test save gym location`() =
        runTest {
            val gymLocation = GymLocationsProvider.createGymLocation()

            coEvery { gymLocationsRepository.save(gymLocation) } returns gymLocation

            val result = gymLocationsService.saveGymLocation(gymLocation)

            assertThat(result).isEqualTo(gymLocation)

            coVerify { gymLocationsRepository.save(gymLocation) }
        }

    @Test
    fun `test update gym location`() =
        runTest {
            val gymLocationA = GymLocationsProvider.createGymLocation()

            coEvery { gymLocationsRepository.existsById(gymLocationA.id ?: "") } returns true
            coEvery { gymLocationsRepository.save(gymLocationA) } returns gymLocationA

            gymLocationsService.updateGymLocation(gymLocationA)

            coVerify { gymLocationsRepository.existsById(gymLocationA.id ?: "") }

            coVerify { gymLocationsRepository.save(gymLocationA) }
        }

    @Test
    fun `test delete gym location by id`() =
        runTest {
            coEvery { gymLocationsRepository.deleteById("1") } just Runs

            gymLocationsService.deleteGymLocationById("1")

            coVerify { gymLocationsRepository.deleteById("1") }
        }
}
