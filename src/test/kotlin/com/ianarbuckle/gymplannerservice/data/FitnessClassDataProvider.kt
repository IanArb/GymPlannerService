package com.ianarbuckle.gymplannerservice.data

import com.ianarbuckle.gymplannerservice.fitnessclass.data.Duration
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import java.time.DayOfWeek
import java.time.LocalDateTime

object FitnessClassDataProvider {

    fun createFitnessClasses(): List<FitnessClass> {
        return listOf(createClass())
    }

    fun createClass(): FitnessClass {
        return FitnessClass(
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalDateTime.of(
                2024,
                10 ,
                1,
                1 ,
                0,
                0,
                0
            ),
            duration = Duration(
                value = 1,
                unit = "SECONDS"
            ),
            description = "Pilates class",
            imageUrl = "",
            name = "Pilates"
        )
    }
}