package com.ianarbuckle.gymplannerservice.fitnessclass.data

import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FitnessClassRepository : CoroutineCrudRepository<FitnessClass, String> {
    @Query("{'dayOfWeek': ?0}")
    suspend fun findFitnessClassesByDayOfWeek(dayOfWeek: DayOfWeek): Flow<FitnessClass>
}
