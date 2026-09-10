package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.availability.PersonalTrainerGateway
import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainerRepository
import org.springframework.stereotype.Component

/**
 * Adapter wiring availability's [PersonalTrainerGateway] port to the trainers
 * feature's repository.
 */
@Component
class AvailabilityPersonalTrainerGateway(
    private val personalTrainerRepository: PersonalTrainerRepository,
) : PersonalTrainerGateway {
    override suspend fun findById(personalTrainerId: String): PersonalTrainer? = personalTrainerRepository.findById(personalTrainerId)
}
