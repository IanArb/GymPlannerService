package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.clients.data.Client
import com.ianarbuckle.gymplannerservice.clients.data.GymPlan
import com.ianarbuckle.gymplannerservice.clients.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.clients.data.Session
import com.ianarbuckle.gymplannerservice.clients.data.Weight
import com.ianarbuckle.gymplannerservice.clients.data.Workout
import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import java.time.LocalDateTime

object ClientsDataProvider {
    fun createClient(
        id: String = "123456789",
        firstName: String = "John",
        lastName: String = "Doe",
        strengthLevel: String = "advanced",
        gymPlan: GymPlan = createGymPlan(),
    ): Client =
        Client(
            id = id,
            firstName = firstName,
            surname = lastName,
            strengthLevel = strengthLevel,
            gymPlan = gymPlan,
        )

    fun createGymPlan(
        name: String = "Basic",
        startDate: LocalDateTime = LocalDateTime.of(2025, 1, 1, 0, 0),
        endDate: LocalDateTime = LocalDateTime.of(2025, 2, 1, 0, 0),
        sessions: List<Session> = createSessions(),
        personalTrainer: PersonalTrainer = createPersonalTrainer(),
    ): GymPlan =
        GymPlan(
            name = name,
            personalTrainer = personalTrainer,
            sessions = sessions,
            startDate = startDate,
            endDate = endDate,
        )

    fun createSessions(
        name: String = "Chest",
        workouts: List<Workout> = createWorkouts(),
    ): List<Session> =
        listOf(
            createSession(
                name = name,
                workouts = workouts,
            ),
        )

    fun createSession(
        name: String = "Chest",
        workouts: List<Workout> =
            createWorkouts(
                name = name,
                sets = 5,
                reps = 5,
                weight = createWeight(),
            ),
    ): Session =
        Session(
            name = name,
            workouts = workouts,
        )

    fun createWorkouts(
        name: String = "Chest",
        sets: Int = 5,
        reps: Int = 5,
        weight: Weight = createWeight(),
    ): List<Workout> =
        listOf(
            createWorkout(
                name = name,
                sets = sets,
                reps = reps,
                weight = weight,
            ),
        )

    fun createWorkout(
        name: String = "Chest",
        sets: Int = 5,
        reps: Int = 5,
        weight: Weight = createWeight(),
    ): Workout =
        Workout(
            name = name,
            sets = sets,
            repetitions = reps,
            weight = weight,
        )

    fun createWeight(
        value: Double = 50.0,
        unit: String = "kg",
    ): Weight =
        Weight(
            value = value,
            unit = unit,
        )

    fun createPersonalTrainer(
        id: String = "123456789",
        firstName: String = "John",
        lastName: String = "Doe",
        socials: Map<String, String> = createSocials(),
    ): PersonalTrainer =
        PersonalTrainer(
            id = id,
            firstName = firstName,
            surname = lastName,
            imageUrl = "www.google.com",
            bio = "Hello",
            socials = socials,
            qualifications = emptyList(),
            gymLocation = GymLocation.CLONTARF
        )

    fun createSocials(
        instagram: String = "ianarbuckle",
        twitter: String = "ianarbuckle",
        facebook: String = "ianarbuckle",
        tiktok: String = "ianarbuckle",
    ): Map<String, String> =
        mapOf(
            "instagram" to instagram,
            "twitter" to twitter,
            "facebook" to facebook,
            "tiktok" to tiktok,
        )
}
