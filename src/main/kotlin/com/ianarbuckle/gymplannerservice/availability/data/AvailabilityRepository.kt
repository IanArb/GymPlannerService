package com.ianarbuckle.gymplannerservice.availability.data

import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AvailabilityRepository : CoroutineCrudRepository<Availability, String> {
    suspend fun findByPersonalTrainerIdAndMonth(
        personalTrainerId: String,
        month: String,
    ): Availability?

    suspend fun existsByPersonalTrainerId(personalTrainerId: String): Boolean

    @Query("{ 'slots.times.id': ?0 }") suspend fun findByTimeId(timeId: String): Availability?
}
