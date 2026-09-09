package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.booking.PersonalTrainerGateway
import org.springframework.stereotype.Component

/**
 * Implements booking's [PersonalTrainerGateway] port using this feature's
 * repository, so :booking can verify a trainer exists without depending on
 * :trainers.
 */
@Component
class PersonalTrainerGatewayImpl(
    private val personalTrainerRepository: PersonalTrainerRepository,
) : PersonalTrainerGateway {
    override suspend fun existsById(id: String): Boolean = personalTrainerRepository.existsById(id)
}
