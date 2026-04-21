package com.ianarbuckle.gymplannerservice.checkin.data

import com.google.common.truth.Truth
import com.ianarbuckle.gymplannerservice.checkin.exception.InvalidCheckOutTimeException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedOutException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotFoundException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotScheduledException
import com.ianarbuckle.gymplannerservice.mocks.CheckInDataProvider
import com.ianarbuckle.gymplannerservice.mocks.PersonalTrainerDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.trainers.data.TrainerAvailabilityStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CheckInServiceTests {

    private val checkInRepository: CheckInRepository = mockk()
    private val personalTrainerRepository: PersonalTrainerRepository = mockk()
    private val service = CheckInServiceImpl(checkInRepository, personalTrainerRepository)

    @Test
    fun `should save check-in with ON_TIME status when trainer checks in on time`() = runTest {
        val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
        val checkInTime = LocalDateTime.of(2026, 4, 21, 9, 0)
        val expected = CheckInDataProvider.createCheckIn(checkInTime = checkInTime)

        coEvery { personalTrainerRepository.findById("1") } returns trainer
        coEvery {
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                "1",
                checkInTime.toLocalDate().atStartOfDay(),
                checkInTime.toLocalDate().atStartOfDay().plusDays(1),
            )
        } returns null
        coEvery { checkInRepository.save(any()) } returns expected
        coEvery { personalTrainerRepository.save(any()) } returns trainer

        val result = service.checkIn("1", checkInTime)

        Truth.assertThat(result.status).isEqualTo(CheckInStatus.ON_TIME)
        coVerify { checkInRepository.save(any()) }
    }

    @Test
    fun `should save check-in with LATE status when trainer checks in after shift start`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
            val checkInTime = LocalDateTime.of(2026, 4, 21, 10, 30)
            val expected =
                CheckInDataProvider.createCheckIn(
                    checkInTime = checkInTime,
                    status = CheckInStatus.LATE
                )

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkInTime.toLocalDate().atStartOfDay(),
                    checkInTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns null
            coEvery { checkInRepository.save(any()) } returns expected
            coEvery { personalTrainerRepository.save(any()) } returns trainer

            val result = service.checkIn("1", checkInTime)

            Truth.assertThat(result.status).isEqualTo(CheckInStatus.LATE)
        }

    @Test
    fun `should update trainer availability status to AVAILABLE after check-in`() = runTest {
        val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
        val checkInTime = LocalDateTime.of(2026, 4, 21, 9, 0)
        val expected = CheckInDataProvider.createCheckIn(checkInTime = checkInTime)
        val availableTrainer =
            trainer.copy(
                availabilityStatus = TrainerAvailabilityStatus.AVAILABLE,
            )

        coEvery { personalTrainerRepository.findById("1") } returns trainer
        coEvery {
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                "1",
                checkInTime.toLocalDate().atStartOfDay(),
                checkInTime.toLocalDate().atStartOfDay().plusDays(1),
            )
        } returns null
        coEvery { checkInRepository.save(any()) } returns expected
        coEvery { personalTrainerRepository.save(availableTrainer) } returns availableTrainer

        service.checkIn("1", checkInTime)

        coVerify { personalTrainerRepository.save(availableTrainer) }
    }

    @Test
    fun `should throw TrainerNotFoundException when trainer does not exist`() = runTest {
        coEvery { personalTrainerRepository.findById("999") } returns null

        assertThrows<TrainerNotFoundException> {
            service.checkIn("999", LocalDateTime.of(2026, 4, 21, 9, 0))
        }
    }

    @Test
    fun `should throw TrainerNotScheduledException when trainer has no shift on that day`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer(schedule = emptyList())
            val checkInTime = LocalDateTime.of(2026, 4, 21, 9, 0)

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkInTime.toLocalDate().atStartOfDay(),
                    checkInTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns null

            assertThrows<TrainerNotScheduledException> { service.checkIn("1", checkInTime) }
        }

    @Test
    fun `should throw TrainerNotScheduledException when trainer checks in after shift ends`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
            val checkInTime = LocalDateTime.of(2026, 4, 21, 17, 0)

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkInTime.toLocalDate().atStartOfDay(),
                    checkInTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns null

            assertThrows<TrainerNotScheduledException> { service.checkIn("1", checkInTime) }
        }

    @Test
    fun `should throw TrainerAlreadyCheckedInException when trainer already checked in today`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
            val checkInTime = LocalDateTime.of(2026, 4, 21, 9, 0)
            val existing = CheckInDataProvider.createCheckIn(checkInTime = checkInTime)

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkInTime.toLocalDate().atStartOfDay(),
                    checkInTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns existing

            assertThrows<TrainerAlreadyCheckedInException> { service.checkIn("1", checkInTime) }
        }

    @Test
    fun `should save check-out and set trainer status to UNAVAILABLE`() = runTest {
        val trainer =
            PersonalTrainerDataProvider.createPersonalTrainer(
                availabilityStatus = TrainerAvailabilityStatus.AVAILABLE,
            )
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
        val existing = CheckInDataProvider.createCheckIn()
        val updated = existing.copy(checkOutTime = checkOutTime)

        coEvery { personalTrainerRepository.findById("1") } returns trainer
        coEvery {
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                "1",
                checkOutTime.toLocalDate().atStartOfDay(),
                checkOutTime.toLocalDate().atStartOfDay().plusDays(1),
            )
        } returns existing
        coEvery { checkInRepository.save(updated) } returns updated
        coEvery { personalTrainerRepository.save(any()) } returns
            trainer.copy(
                availabilityStatus = TrainerAvailabilityStatus.UNAVAILABLE,
            )

        val result = service.checkOut("1", checkOutTime)

        Truth.assertThat(result.checkOutTime).isEqualTo(checkOutTime)
        coVerify {
            personalTrainerRepository.save(
                match { it.availabilityStatus == TrainerAvailabilityStatus.UNAVAILABLE }
            )
        }
    }

    @Test
    fun `should throw TrainerNotFoundException on check-out when trainer does not exist`() =
        runTest {
            coEvery { personalTrainerRepository.findById("999") } returns null

            assertThrows<TrainerNotFoundException> {
                service.checkOut("999", LocalDateTime.of(2026, 4, 21, 17, 0))
            }
        }

    @Test
    fun `should throw TrainerNotCheckedInException when no check-in exists today`() = runTest {
        val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
        val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)

        coEvery { personalTrainerRepository.findById("1") } returns trainer
        coEvery {
            checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                "1",
                checkOutTime.toLocalDate().atStartOfDay(),
                checkOutTime.toLocalDate().atStartOfDay().plusDays(1),
            )
        } returns null

        assertThrows<TrainerNotCheckedInException> { service.checkOut("1", checkOutTime) }
    }

    @Test
    fun `should throw InvalidCheckOutTimeException when check-out time is before check-in time`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
            val checkInTime = LocalDateTime.of(2026, 4, 21, 9, 0)
            val checkOutTime = LocalDateTime.of(2026, 4, 21, 8, 0)
            val existing = CheckInDataProvider.createCheckIn(checkInTime = checkInTime)

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkOutTime.toLocalDate().atStartOfDay(),
                    checkOutTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns existing

            assertThrows<InvalidCheckOutTimeException> { service.checkOut("1", checkOutTime) }
        }

    @Test
    fun `should throw TrainerAlreadyCheckedOutException when trainer already checked out`() =
        runTest {
            val trainer = PersonalTrainerDataProvider.createPersonalTrainer()
            val checkOutTime = LocalDateTime.of(2026, 4, 21, 17, 0)
            val existing = CheckInDataProvider.createCheckIn(checkOutTime = checkOutTime)

            coEvery { personalTrainerRepository.findById("1") } returns trainer
            coEvery {
                checkInRepository.findByTrainerIdAndCheckInTimeBetween(
                    "1",
                    checkOutTime.toLocalDate().atStartOfDay(),
                    checkOutTime.toLocalDate().atStartOfDay().plusDays(1),
                )
            } returns existing

            assertThrows<TrainerAlreadyCheckedOutException> { service.checkOut("1", checkOutTime) }
        }
}
