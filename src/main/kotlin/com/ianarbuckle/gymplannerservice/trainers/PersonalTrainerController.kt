package com.ianarbuckle.gymplannerservice.trainers

import com.ianarbuckle.gymplannerservice.trainers.data.GymLocation
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainer
import com.ianarbuckle.gymplannerservice.trainers.data.PersonalTrainersService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/personal_trainers")
@Tag(
    name = "Personal Trainers",
    description = "Endpoints for personal trainers",
)
class PersonalTrainerController(
    private val service: PersonalTrainersService,
) {
    @Operation(
        summary = "Get personal trainers by gym location",
        description = "Retrieve all personal trainers by gym location",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval of personal trainers",
            ),
        ],
    )
    @GetMapping
    fun findTrainersByGymLocation(
        @Parameter(
            description = "Gym location",
            required = true,
            schema = Schema(implementation = GymLocation::class),
        )
        @RequestParam gymLocation: GymLocation,
    ): Flow<PersonalTrainer> = service.findTrainersByGymLocation(gymLocation)

    @Operation(summary = "Find a personal trainer", description = "Find a new personal trainer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Personal trainer is found",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Personal trainer is not found",
            ),
        ],
    )
    @GetMapping("{id}")
    suspend fun findTrainerById(
        @PathVariable id: String,
    ) {
        service.findById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found")
    }

    @Operation(summary = "Create a personal trainer", description = "Create a new personal trainer")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Personal trainer created successfully",
            ),
        ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTrainer(
        @Parameter(
            description = "Personal trainer details to be created",
            required = true,
            schema = Schema(implementation = PersonalTrainer::class),
        )
        @Valid
        @RequestBody personalTrainer: PersonalTrainer,
    ) = service.createTrainer(personalTrainer)

    @Operation(
        summary = "Update a personal trainer",
        description = "Update an existing personal trainer",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Personal trainer updated successfully",
            ),
        ],
    )
    @PutMapping
    suspend fun updateTrainer(
        @Parameter(
            description = "Personal trainer details to be updated",
            required = true,
            schema = Schema(implementation = PersonalTrainer::class),
        )
        @Valid
        @RequestBody personalTrainer: PersonalTrainer,
    ) = service.updateTrainer(personalTrainer)

    @Operation(
        summary = "Delete a personal trainer by ID",
        description = "Delete a personal trainer by its ID",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Personal trainer deleted successfully",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Personal trainer not found",
            ),
        ],
    )
    @DeleteMapping("{id}")
    suspend fun deleteTrainerById(
        @Parameter(
            description = "ID of the personal trainer to be deleted",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable id: String,
    ) = service.deleteTrainerById(id)
}
