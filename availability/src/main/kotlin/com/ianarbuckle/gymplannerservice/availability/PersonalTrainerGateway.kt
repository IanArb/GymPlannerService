package com.ianarbuckle.gymplannerservice.availability

import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainer

/**
 * Port for the personal-trainer lookup availability needs (fetching a trainer by
 * id). Implemented in :app so :availability does not depend on :trainers.
 */
interface PersonalTrainerGateway {
    suspend fun findById(personalTrainerId: String): PersonalTrainer?
}
