package com.ianarbuckle.gymplannerservice.facilityStatus

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.MachineStatus
import com.ianarbuckle.gymplannerservice.mocks.FacilityStatusDataProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class FacilityStatusServiceTests {
    private val facilityStatusRepository: FacilityStatusRepository = mockk()

    private val facilityStatusService: FacilityStatusService =
        FacilityStatusServiceImpl(facilityStatusRepository)

    @Test
    fun `should return all machines`() = runTest {
        // Given
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    machineNumber = 2,
                ),
            )
        coEvery { facilityStatusRepository.findAll() } returns flowOf(*facilities.toTypedArray())

        // When & Then
        facilityStatusService.findAllMachines().test {
            assertThat(awaitItem()).isEqualTo(facilities.first())
            assertThat(awaitItem()).isEqualTo(facilities.last())
            awaitComplete()
        }

        coVerify { facilityStatusRepository.findAll() }
    }

    @Test
    fun `should return machines by gym location`() = runTest {
        // Given
        val gymLocation = GymLocation.CLONTARF
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(gymLocation = gymLocation),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    gymLocation = gymLocation,
                ),
            )
        coEvery { facilityStatusRepository.findMachinesByGymLocation(gymLocation) } returns
            flowOf(*facilities.toTypedArray())

        // When & Then
        facilityStatusService.findMachinesByGymLocation(gymLocation).test {
            assertThat(awaitItem()).isEqualTo(facilities.first())
            assertThat(awaitItem()).isEqualTo(facilities.last())
            awaitComplete()
        }

        coVerify { facilityStatusRepository.findMachinesByGymLocation(gymLocation) }
    }

    @Test
    fun `should return machines by status`() = runTest {
        // Given
        val status = MachineStatus.OPERATIONAL.name
        val facilities =
            listOf(
                FacilityStatusDataProvider.createFacilityStatus(status = MachineStatus.OPERATIONAL),
                FacilityStatusDataProvider.createFacilityStatus(
                    id = "2",
                    machineName = "Rowing Machine",
                    status = MachineStatus.OPERATIONAL,
                ),
            )
        coEvery { facilityStatusRepository.findAllMachinesByStatus(status) } returns
            flowOf(*facilities.toTypedArray())

        // When & Then
        facilityStatusService.findMachinesByStatus(status).test {
            assertThat(awaitItem()).isEqualTo(facilities.first())
            assertThat(awaitItem()).isEqualTo(facilities.last())
            awaitComplete()
        }

        coVerify { facilityStatusRepository.findAllMachinesByStatus(status) }
    }

    @Test
    fun `should return machine by id`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        coEvery { facilityStatusRepository.findById("1") } returns facility

        // When
        val result = facilityStatusService.findMachineById("1")

        // Then
        assertThat(result).isEqualTo(facility)
        coVerify { facilityStatusRepository.findById("1") }
    }

    @Test
    fun `should return null when machine not found by id`() = runTest {
        // Given
        coEvery { facilityStatusRepository.findById("999") } returns null

        // When
        val result = facilityStatusService.findMachineById("999")

        // Then
        assertThat(result).isNull()
        coVerify { facilityStatusRepository.findById("999") }
    }

    @Test
    fun `should save facility`() = runTest {
        // Given
        val facility = FacilityStatusDataProvider.createFacilityStatus()
        coEvery { facilityStatusRepository.save(facility) } returns facility

        // When
        facilityStatusService.saveFacility(facility)

        // Then
        coVerify { facilityStatusRepository.save(facility) }
    }

    @Test
    fun `should delete facility by id`() = runTest {
        // Given
        coEvery { facilityStatusRepository.deleteById("1") } returns Unit

        // When
        facilityStatusService.deleteFacilityById("1")

        // Then
        coVerify { facilityStatusRepository.deleteById("1") }
    }

    @Test
    fun `should update facility`() = runTest {
        // Given
        val updated =
            FacilityStatusDataProvider.createFacilityStatus(status = MachineStatus.OUT_OF_ORDER)
        coEvery { facilityStatusRepository.save(updated) } returns updated

        // When
        val result = facilityStatusService.updateFacility(updated)

        // Then
        assertThat(result).isEqualTo(updated)
        coVerify { facilityStatusRepository.save(updated) }
    }
}
