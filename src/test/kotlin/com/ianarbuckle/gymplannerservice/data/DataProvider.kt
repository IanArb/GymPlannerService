package com.ianarbuckle.gymplannerservice.data

import com.ianarbuckle.gymplannerservice.model.*
import java.time.LocalDateTime

object DataProvider {

    fun createClient(
        id: String = "123456789",
        firstName: String = "John",
        lastName: String = "Doe",
        strengthLevel: String = "advanced",
        gymPlan: GymPlan = createGymPlan()
    ): Client {
        return Client(
            id = id,
            firstName = firstName,
            surname = lastName,
            strengthLevel = strengthLevel,
            gymPlan = gymPlan,
        )
    }

    fun createGymPlan(
        name: String = "Basic",
        startDate: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0),
        endDate: LocalDateTime = LocalDateTime.of(2024, 2, 1, 0, 0),
        sessions: List<Session> = createSessions(),
        personalTrainer: PersonalTrainer = createPersonalTrainer()
    ): GymPlan {
        return GymPlan(
            name = name,
            personalTrainer = personalTrainer,
            sessions = sessions,
            startDate = startDate,
            endDate = endDate
        )
    }

    fun createSessions(
        name: String = "Chest",
        workouts: List<Workout> = createWorkouts()
    ): List<Session> {
        return listOf(createSession(
            name = name,
            workouts = workouts
        ))
    }

    fun createSession(
        name: String = "Chest",
        workouts: List<Workout> = createWorkouts(
            name = name,
            sets = 5,
            reps = 5,
            weight = createWeight()
        ),
    ): Session {
        return Session(
            name = name,
            workouts = workouts
        )
    }

    fun createWorkouts(
        name: String = "Chest",
        sets: Int = 5,
        reps: Int = 5,
        weight: Weight = createWeight()
    ): List<Workout> {
        return listOf(createWorkout(
            name = name,
            sets = sets,
            reps = reps,
            weight = weight,
        ))
    }

    fun createWorkout(
        name: String = "Chest",
        sets: Int = 5,
        reps: Int = 5,
        weight: Weight = createWeight()
    ): Workout {
        return Workout(
            name = name,
            sets = sets,
            repetitions = reps,
            weight = weight,
        )
    }

    fun createWeight(
        value: Double = 50.0,
        unit: String = "kg"
    ): Weight {
        return Weight(
            value = value,
            unit = unit,
        )
    }

    fun createPersonalTrainer(
        id: String = "123456789",
        firstName: String = "John",
        lastName: String = "Doe",
        socials: Map<String, String> = createSocials()
    ): PersonalTrainer {
        return PersonalTrainer(
            id = id,
            firstName = firstName,
            surname = lastName,
            socials = socials,
        )
    }

    fun createSocials(
        instagram: String = "ianarbuckle",
        twitter: String = "ianarbuckle",
        facebook: String = "ianarbuckle",
        tiktok: String = "ianarbuckle",
    ): Map<String, String> {
        return mapOf(
            "instagram" to instagram,
            "twitter" to twitter,
            "facebook" to facebook,
            "tiktok" to tiktok,
        )
    }

}
