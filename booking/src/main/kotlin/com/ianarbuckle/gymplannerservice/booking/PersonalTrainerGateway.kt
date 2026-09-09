package com.ianarbuckle.gymplannerservice.booking

/**
 * Port for the personal-trainer lookup booking needs (verifying a trainer exists).
 * Implemented by the trainers feature so :booking does not depend on it.
 */
interface PersonalTrainerGateway {
    suspend fun existsById(id: String): Boolean
}
