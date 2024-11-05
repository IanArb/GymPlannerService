package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainersService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/personal_trainers")
class PersonalTrainerController(private val service: PersonalTrainersService) {

    @GetMapping
    fun findTrainersByGymLocation(@RequestParam gymLocation: GymLocation): Flow<PersonalTrainer> {
        return service.findTrainersByGymLocation(gymLocation)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTrainer(@Valid @RequestBody personalTrainer: PersonalTrainer) = service.createTrainer(personalTrainer)

    @PutMapping
    suspend fun updateTrainer(@Valid @RequestBody personalTrainer: PersonalTrainer) = service.updateTrainer(personalTrainer)

    @DeleteMapping("{id}")
    suspend fun deleteTrainerById(@PathVariable id: String) = service.deleteTrainerById(id)
}
