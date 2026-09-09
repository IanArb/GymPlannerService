package com.ianarbuckle.gymplannerservice.exercises

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ExercisesRepository : CoroutineCrudRepository<Exercise, String>
