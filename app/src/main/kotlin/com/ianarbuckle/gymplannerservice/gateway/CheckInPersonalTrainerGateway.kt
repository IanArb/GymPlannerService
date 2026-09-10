package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.checkin.PersonalTrainerGateway
import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainerRepository
import org.springframework.stereotype.Component

/**
 * Adapter wiring check-in's [PersonalTrainerGateway] port to the trainers feature's
 * repository.
 */
@Component
class CheckInPersonalTrainerGateway(
    private val personalTrainerRepository: PersonalTrainerRepository,
) : PersonalTrainerGateway {
    override suspend fun findById(trainerId: String): PersonalTrainer? = personalTrainerRepository.findById(trainerId)

    override suspend fun save(personalTrainer: PersonalTrainer): PersonalTrainer = personalTrainerRepository.save(personalTrainer)
}
