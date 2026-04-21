package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.ScheduledShift
import com.ianarbuckle.gymplannerservice.trainers.data.TrainerAvailabilityStatus
import java.time.DayOfWeek
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object PersonalTrainerDataProvider {
    fun createPersonalTrainer(
        id: String = "1",
        firstName: String = "John",
        lastName: String = "Doe",
        imageUrl: String = "https://www.johndoe.com",
        description: String = "John Doe Description",
        location: GymLocation = GymLocation.CLONTARF,
        qualifications: List<String> =
            "Qualification 1, Qualification 2, Qualification 3".split(", "),
        schedule: List<ScheduledShift> =
            listOf(
                ScheduledShift(
                    dayOfWeek = DayOfWeek.TUESDAY,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(17, 0),
                ),
            ),
        availabilityStatus: TrainerAvailabilityStatus = TrainerAvailabilityStatus.UNAVAILABLE,
    ): PersonalTrainer =
        PersonalTrainer(
            id = id,
            firstName = firstName,
            lastName = lastName,
            imageUrl = imageUrl,
            bio = description,
            gymLocation = location,
            qualifications = qualifications,
            schedule = schedule,
            availabilityStatus = availabilityStatus,
        )

    fun personalTrainers(): Flow<PersonalTrainer> =
        flowOf(
            createPersonalTrainer(),
            createPersonalTrainer(
                id = "2",
                firstName = "Jane",
                lastName = "Doe",
                imageUrl = "https://www.janedoe.com",
                description = "Jane Doe Description",
                location = GymLocation.CLONTARF,
                qualifications = "Qualification 4, Qualification 5, Qualification 6".split(", "),
            ),
        )
}
