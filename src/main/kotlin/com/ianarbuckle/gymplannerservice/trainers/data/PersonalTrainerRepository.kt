package com.ianarbuckle.gymplannerservice.trainers.data

import com.ianarbuckle.gymplannerservice.common.GymLocation
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonalTrainerRepository : CoroutineCrudRepository<PersonalTrainer, String> {
    @Query("{'gymLocation': ?0}")
    fun findAllByGymLocation(gymLocation: GymLocation): Flow<PersonalTrainer>

    @Query("{'schedule.dayOfWeek': ?0}")
    fun findAllByScheduleDayOfWeek(dayOfWeek: String): Flow<PersonalTrainer>
}
