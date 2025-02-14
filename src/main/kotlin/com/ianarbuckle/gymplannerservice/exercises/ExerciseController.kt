package com.ianarbuckle.gymplannerservice.exercises

import com.ianarbuckle.gymplannerservice.exercises.data.Exercise
import com.ianarbuckle.gymplannerservice.exercises.data.ExerciseService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/exercises")
@Tag(
    name = "Exercises",
    description = "Endpoints for exercises",
)
class ExerciseController {
    @Autowired
    private lateinit var exerciseService: ExerciseService

    @GetMapping
    fun findExercises(): Flow<Exercise> = exerciseService.findAllExercises()

    @GetMapping("/{id}")
    suspend fun findExerciseById(
        @PathVariable id: String,
    ): Exercise? = exerciseService.findExerciseById(id)

    @PostMapping
    suspend fun createExercise(
        @RequestBody @Valid exercise: Exercise,
    ): Exercise = exerciseService.createExercise(exercise)

    @PutMapping("/{id}")
    suspend fun updateExercise(
        @RequestBody @Valid exercise: Exercise,
    ) = exerciseService.updateExercise(exercise)

    @DeleteMapping("/{id}")
    suspend fun deleteExercise(
        @PathVariable id: String,
    ) = exerciseService.deleteExercise(id)
}
