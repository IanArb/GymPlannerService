package com.ianarbuckle.gymplannerservice.exercises.data

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface ExerciseService {
    fun findAllExercises(): Flow<Exercise>

    suspend fun findExerciseById(id: String): Exercise?

    suspend fun createExercise(exercise: Exercise): Exercise

    suspend fun updateExercise(exercise: Exercise)

    suspend fun deleteExercise(id: String)
}

@Service
class ExerciseServiceImpl(
    private val repository: ExercisesRepository,
) : ExerciseService {
    @Override override fun findAllExercises(): Flow<Exercise> = repository.findAll()

    @Override override suspend fun findExerciseById(id: String): Exercise? = repository.findById(id)

    override suspend fun createExercise(exercise: Exercise): Exercise = repository.save(exercise)

    override suspend fun updateExercise(exercise: Exercise) {
        repository.save(exercise).takeIf { repository.existsById(exercise.id ?: "") }
    }

    override suspend fun deleteExercise(id: String) {
        repository.deleteById(id)
    }
}
