package com.ianarbuckle.gymplannerservice.gateway

import com.ianarbuckle.gymplannerservice.booking.PersonalTrainerGateway
import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainerRepository
import org.springframework.stereotype.Component

/**
 * Adapter wiring booking's [PersonalTrainerGateway] port to the trainers feature's
 * repository.
 */
@Component
class BookingPersonalTrainerGateway(
    private val personalTrainerRepository: PersonalTrainerRepository,
) : PersonalTrainerGateway {
    override suspend fun existsById(id: String): Boolean = personalTrainerRepository.existsById(id)
}
