package com.ianarbuckle.gymplannerservice.exercises

import com.ianarbuckle.gymplannerservice.exercises.data.Exercise
import com.ianarbuckle.gymplannerservice.exercises.data.ExerciseService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/exercises")
class ExerciseController {

    @Autowired
    private lateinit var exerciseService: ExerciseService

    @GetMapping
    fun findExercises(): Flow<Exercise> = exerciseService.findAllExercises()

    @GetMapping("/{id}")
    suspend fun findExerciseById(@PathVariable id: String): Exercise? = exerciseService.findExerciseById(id)

    @PostMapping
    suspend fun createExercise(@RequestBody @Valid exercise: Exercise): Exercise = exerciseService.createExercise(exercise)

    @PutMapping("/{id}")
    suspend fun updateExercise(@RequestBody @Valid exercise: Exercise) = exerciseService.updateExercise(exercise)

    @DeleteMapping("/{id}")
    suspend fun deleteExercise(@PathVariable id: String) = exerciseService.deleteExercise(id)
}