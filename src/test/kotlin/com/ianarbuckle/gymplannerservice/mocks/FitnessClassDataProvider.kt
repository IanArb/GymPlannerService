package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.fitnessclass.data.Duration
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import java.time.DayOfWeek
import java.time.LocalTime

object FitnessClassDataProvider {
    fun createFitnessClasses(): List<FitnessClass> = listOf(createClass())

    fun createClass(): FitnessClass =
        FitnessClass(
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(
                10,
                10,
            ),
            duration = Duration(
                value = 1,
                unit = "SECONDS",
            ),
            description = "Pilates class",
            imageUrl = "www.google.com",
            name = "Pilates",
            endTime = LocalTime.of(
                10,
                10,
            ),
        )
}
