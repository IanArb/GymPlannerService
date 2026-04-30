package com.ianarbuckle.gymplannerservice.trainers

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.mocks.PersonalTrainerDataProvider
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerRepository
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainerServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PersonalTrainerServiceTests {
    private val personalTrainerRepository: PersonalTrainerRepository = mockk()

    private val personalTrainerService = PersonalTrainerServiceImpl(personalTrainerRepository)

    @Test
    fun `test find all personal trainers by gym location`() = runTest {
        val personalTrainers = PersonalTrainerDataProvider.personalTrainers()
        coEvery { personalTrainerRepository.findAllByGymLocation(GymLocation.CLONTARF) } returns
            personalTrainers

        personalTrainerService.findTrainersByGymLocation(GymLocation.CLONTARF).test {
            assertThat(awaitItem()).isEqualTo(personalTrainers.first())
            assertThat(awaitItem()).isEqualTo(personalTrainers.last())
            awaitComplete()
        }

        coVerify { personalTrainerRepository.findAllByGymLocation(GymLocation.CLONTARF) }
    }

    @Test
    fun `test save personal trainer`() = runTest {
        val personalTrainer = PersonalTrainerDataProvider.createPersonalTrainer()
        coEvery { personalTrainerRepository.save(personalTrainer) } returns personalTrainer

        val savedPersonalTrainer = personalTrainerService.createTrainer(personalTrainer)

        assertThat(savedPersonalTrainer).isEqualTo(personalTrainer)

        coVerify { personalTrainerRepository.save(personalTrainer) }
    }

    @Test
    fun `test delete personal trainer by id`() = runTest {
        coEvery { personalTrainerRepository.deleteById("1") } returns Unit

        personalTrainerService.deleteTrainerById("1")

        coVerify { personalTrainerRepository.deleteById("1") }
    }

    @Test
    fun `test update personal trainer`() = runTest {
        val personalTrainer = PersonalTrainerDataProvider.createPersonalTrainer()
        val id = personalTrainer.id ?: ""
        coEvery { personalTrainerRepository.existsById(id) } returns true
        coEvery { personalTrainerRepository.save(personalTrainer) } returns personalTrainer

        personalTrainerService.updateTrainer(personalTrainer)

        coVerify { personalTrainerRepository.save(personalTrainer) }
    }

    @Test
    fun `test find scheduled trainers by date and location returns trainers scheduled on that day`() =
        runTest {
            val date = LocalDate.of(2026, 4, 21) // Tuesday
            val gymLocation = GymLocation.CLONTARF
            val trainers = PersonalTrainerDataProvider.personalTrainers()
            coEvery {
                personalTrainerRepository.findAllByScheduleDayOfWeekAndGymLocation(
                    DayOfWeek.TUESDAY.name,
                    gymLocation,
                )
            } returns trainers

            personalTrainerService.findScheduledTrainersByDate(date, gymLocation).test {
                assertThat(awaitItem()).isEqualTo(trainers.first())
                assertThat(awaitItem()).isEqualTo(trainers.last())
                awaitComplete()
            }

            coVerify {
                personalTrainerRepository.findAllByScheduleDayOfWeekAndGymLocation(
                    DayOfWeek.TUESDAY.name,
                    gymLocation,
                )
            }
        }

    @Test
    fun `test find scheduled trainers by date and location returns empty when none scheduled`() =
        runTest {
            val date = LocalDate.of(2026, 4, 19) // Sunday
            val gymLocation = GymLocation.CLONTARF
            coEvery {
                personalTrainerRepository.findAllByScheduleDayOfWeekAndGymLocation(
                    DayOfWeek.SUNDAY.name,
                    gymLocation,
                )
            } returns flowOf()

            personalTrainerService.findScheduledTrainersByDate(date, gymLocation).test {
                awaitComplete()
            }

            coVerify {
                personalTrainerRepository.findAllByScheduleDayOfWeekAndGymLocation(
                    DayOfWeek.SUNDAY.name,
                    gymLocation,
                )
            }
        }
}
