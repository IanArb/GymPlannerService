package com.ianarbuckle.gymplannerservice.trainers.data

import com.ianarbuckle.gymplannerservice.common.GymLocation
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface PersonalTrainersService {
    fun findTrainersByGymLocation(gymLocation: GymLocation): Flow<PersonalTrainer>

    fun findScheduledTrainersByDate(
        date: LocalDate,
        gymLocation: GymLocation,
    ): Flow<PersonalTrainer>

    suspend fun createTrainer(personalTrainer: PersonalTrainer): PersonalTrainer

    suspend fun updateTrainer(personalTrainer: PersonalTrainer)

    suspend fun deleteTrainerById(id: String)

    suspend fun findById(id: String): PersonalTrainer?
}

@Service
class PersonalTrainerServiceImpl(
    private val repository: PersonalTrainerRepository,
) : PersonalTrainersService {
    override suspend fun createTrainer(personalTrainer: PersonalTrainer): PersonalTrainer =
        repository.save(personalTrainer)

    override suspend fun deleteTrainerById(id: String) = repository.deleteById(id)

    override fun findTrainersByGymLocation(gymLocation: GymLocation): Flow<PersonalTrainer> =
        when (gymLocation) {
            GymLocation.CLONTARF,
            GymLocation.ASTONQUAY,
            GymLocation.DUNLOAGHAIRE,
            GymLocation.WESTMANSTOWN,
            GymLocation.SANDYMOUNT,
            GymLocation.LEOPARDSTOWN, -> repository.findAllByGymLocation(gymLocation)
        }

    override suspend fun updateTrainer(personalTrainer: PersonalTrainer) {
        repository.save(personalTrainer).takeIf { repository.existsById(personalTrainer.id ?: "") }
    }

    override fun findScheduledTrainersByDate(
        date: LocalDate,
        gymLocation: GymLocation,
    ): Flow<PersonalTrainer> =
        repository.findAllByScheduleDayOfWeekAndGymLocation(date.dayOfWeek.name, gymLocation)

    override suspend fun findById(id: String): PersonalTrainer? = repository.findById(id)
}
