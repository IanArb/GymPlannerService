package com.ianarbuckle.gymplannerservice.availability.data

import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import com.ianarbuckle.gymplannerservice.mocks.AvailabilityDataProvider
import com.ianarbuckle.gymplannerservice.mocks.PersonalTrainerDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AvailabilityServiceTests {
    private val availabilityRepository = mockk<AvailabilityRepository>()
    private val personalTrainerRepository = mockk<PersonalTrainerRepository>()

    private val fixedClock = Clock.fixed(Instant.parse("2025-12-01T08:00:00Z"), ZoneId.of("UTC"))

    private val availabilityService =
        AvailabilityServiceImpl(
            availabilityRepository = availabilityRepository,
            personalTrainerRepository = personalTrainerRepository,
            clock = fixedClock,
        )

    @Test
    fun `should return availability when found`() =
        runTest {
            val availability = AvailabilityDataProvider.createAvailability()
            val personalTrainer =
                PersonalTrainerDataProvider.createPersonalTrainer(
                    id = availability.personalTrainerId,
                )

            coEvery { personalTrainerRepository.findById(any()) } returns personalTrainer
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(any(), any()) } returns availability
            coEvery { availabilityRepository.save(availability) } returns availability

            val result =
                availabilityService.getAvailability(
                    personalTrainerId = availability.personalTrainerId,
                    month = availability.month,
                )
            coVerify(exactly = 0) { availabilityRepository.save(availability) }
            assertEquals(availability, result)
        }

    @Test
    fun `should return availability AND update the availability when date is before current date`() =
        runTest {
            val availability =
                AvailabilityDataProvider.createAvailability(
                    slots = AvailabilityDataProvider.createAppointmentSlots(
                        date = LocalDate.of(2021, 10, 10),
                        times = listOf(
                            AvailabilityDataProvider.createTime(
                                status = Status.UNAVAILABLE,
                            ),
                        ),
                    ),
                )
            val personalTrainer =
                PersonalTrainerDataProvider.createPersonalTrainer(
                    id = availability.personalTrainerId,
                )

            coEvery { personalTrainerRepository.findById(any()) } returns personalTrainer
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(any(), any()) } returns availability
            coEvery { availabilityRepository.save(availability) } returns availability

            val result =
                availabilityService.getAvailability(
                    personalTrainerId = availability.personalTrainerId,
                    month = availability.month,
                )
            coVerify(exactly = 1) { availabilityRepository.save(availability) }
            assertEquals(availability, result)
        }

    @Test
    fun `test getAvailability should throw AvailabilityNotFoundException when availability is not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"

            coEvery { personalTrainerRepository.findById(personalTrainerId) } returns mockk()
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(personalTrainerId, month) } returns null

            assertFailsWith<AvailabilityNotFoundException> {
                availabilityService.getAvailability(personalTrainerId, month)
            }
        }

    @Test
    fun `test getAvailability should throw PersonalTrainerNotFoundException when personal trainer is not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"

            coEvery { personalTrainerRepository.findById(personalTrainerId) } returns null

            assertFailsWith<PersonalTrainerNotFoundException> {
                availabilityService.getAvailability(personalTrainerId, month)
            }
        }

    @Test
    fun `should save availability`() =
        runTest {
            val availability = AvailabilityDataProvider.createAvailability()
            val personalTrainer =
                PersonalTrainerDataProvider.createPersonalTrainer(
                    id = availability.personalTrainerId,
                )

            coEvery { personalTrainerRepository.findById(any()) } returns personalTrainer
            coEvery { availabilityRepository.save(any()) } returns availability

            val result = availabilityService.saveAvailability(availability)
            assertEquals(availability, result)
        }

    @Test
    fun `should update availability`() =
        runTest {
            val availability = AvailabilityDataProvider.createAvailability()

            coEvery { availabilityRepository.save(any()) } returns availability
            coEvery { availabilityRepository.existsByPersonalTrainerId(any()) } returns true

            availabilityService.updateAvailability(availability)

            coVerify { availabilityRepository.save(availability) }
        }

    @Test
    fun `test isAvailable should delete availability`() =
        runTest {
            val personalTrainerId = "trainer1"

            coEvery { availabilityRepository.deleteById(personalTrainerId) } returns Unit

            availabilityService.deleteAvailability(personalTrainerId)

            coVerify { availabilityRepository.deleteById(personalTrainerId) }
        }

    @Test
    fun `test isAvailable should return true when personal trainer is available`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2025-12"
            val date = "2025-12-01"
            val time = "08:00"
            val endTime = "09:00"

            val availability =
                AvailabilityDataProvider.createAvailability(
                    personalTrainerId = personalTrainerId,
                    month = month,
                    slots = AvailabilityDataProvider.createAppointmentSlots(
                        date = LocalDate.parse(date),
                        times = listOf(
                            AvailabilityDataProvider.createTime(
                                startTime = LocalTime.parse(time),
                                endTime = LocalTime.parse(endTime),
                                status = Status.AVAILABLE,
                            ),
                        ),
                    ),
                )
            val personalTrainer = PersonalTrainerDataProvider.createPersonalTrainer(id = personalTrainerId)

            coEvery { personalTrainerRepository.findById(any()) } returns personalTrainer
            coEvery { availabilityRepository.save(availability) } returns availability
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(any(), any()) } returns availability

            val result =
                availabilityService.isAvailable(
                    personalTrainerId = availability.personalTrainerId,
                    month = month,
                )
            assertTrue(result.isAvailable)
        }

    @Suppress("MaxLineLength")
    @Test
    fun `test isAvailable should return false when personal trainer is not available`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            val date = "2023-12-01"
            val time = "08:00"
            val endTime = "09:00"

            val availability =
                AvailabilityDataProvider.createAvailability(
                    personalTrainerId = personalTrainerId,
                    month = month,
                    slots = AvailabilityDataProvider.createAppointmentSlots(
                        date = LocalDate.parse(date),
                        times = listOf(
                            AvailabilityDataProvider.createTime(
                                startTime = LocalTime.parse(time),
                                endTime = LocalTime.parse(endTime),
                                status = Status.UNAVAILABLE,
                            ),
                        ),
                    ),
                )

            coEvery { personalTrainerRepository.findById(personalTrainerId) } returns mockk()
            coEvery { availabilityRepository.save(availability) } returns availability
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(personalTrainerId, month) } returns availability

            val result = availabilityService.isAvailable(personalTrainerId, month)
            assertFalse(result.isAvailable)
        }

    @Test
    fun `test isAvailable should throw AvailabilityNotFoundException when availability is not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            val date = "2023-12-01"
            val time = "08:00"

            coEvery { personalTrainerRepository.findById(personalTrainerId) } returns mockk()
            coEvery { availabilityRepository.findByPersonalTrainerIdAndMonth(personalTrainerId, month) } returns null

            assertFailsWith<AvailabilityNotFoundException> {
                availabilityService.isAvailable(personalTrainerId, month)
            }
        }

    @Test
    fun `test isAvailable should throw PersonalTrainerNotFoundException when personal trainer is not found`() =
        runTest {
            val personalTrainerId = "trainer1"
            val month = "2023-12"
            val date = "2023-12-01"

            coEvery { personalTrainerRepository.findById(personalTrainerId) } returns null

            assertFailsWith<PersonalTrainerNotFoundException> {
                availabilityService.isAvailable(personalTrainerId, month)
            }
        }
}
