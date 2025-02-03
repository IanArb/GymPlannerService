package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainersService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/personal_trainers")
class PersonalTrainerController(
    private val service: PersonalTrainersService,
) {
    @GetMapping
    fun findTrainersByGymLocation(
        @RequestParam gymLocation: GymLocation,
    ): Flow<PersonalTrainer> = service.findTrainersByGymLocation(gymLocation)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTrainer(
        @Valid @RequestBody personalTrainer: PersonalTrainer,
    ) = service.createTrainer(personalTrainer)

    @PutMapping
    suspend fun updateTrainer(
        @Valid @RequestBody personalTrainer: PersonalTrainer,
    ) = service.updateTrainer(personalTrainer)

    @DeleteMapping("{id}")
    suspend fun deleteTrainerById(
        @PathVariable id: String,
    ) = service.deleteTrainerById(id)
}
