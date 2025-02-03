package com.ianarbuckle.gymplannerservice.exercises.data

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExercisesRepository : CoroutineCrudRepository<Exercise, String>
