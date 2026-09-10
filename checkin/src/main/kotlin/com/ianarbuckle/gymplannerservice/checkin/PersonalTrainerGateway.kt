package com.ianarbuckle.gymplannerservice.checkin

import com.ianarbuckle.gymplannerservice.trainers.PersonalTrainer

/**
 * Port for the trainer lookup/update check-in needs (reading a trainer's schedule
 * and persisting their availability status). Implemented by an adapter in :app so
 * :checkin does not depend on :trainers.
 */
interface PersonalTrainerGateway {
    suspend fun findById(trainerId: String): PersonalTrainer?

    suspend fun save(personalTrainer: PersonalTrainer): PersonalTrainer
}
