package com.ianarbuckle.gymplannerservice.trainers.data

import com.ianarbuckle.gymplannerservice.clients.data.PersonalTrainer
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface PersonalTrainersService {
    fun findAllTrainers(): Flow<PersonalTrainer>

    suspend fun createTrainer(personalTrainer: PersonalTrainer): PersonalTrainer

    suspend fun deleteTrainerById(id: String)
}

@Service
class DefaultPersonalTrainerService(
    private val personalTrainerRepository: PersonalTrainerRepository,
) : PersonalTrainersService {
    override fun findAllTrainers(): Flow<PersonalTrainer> = personalTrainerRepository.findAll()

    override suspend fun createTrainer(personalTrainer: PersonalTrainer): PersonalTrainer = personalTrainerRepository.save(personalTrainer)

    override suspend fun deleteTrainerById(id: String) = personalTrainerRepository.deleteById(id)
}
