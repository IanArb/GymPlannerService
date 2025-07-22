package com.ianarbuckle.gymplannerservice.fitnessclass

import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClass
import com.ianarbuckle.gymplannerservice.fitnessclass.data.FitnessClassesService
import com.ianarbuckle.gymplannerservice.fitnessclass.exception.NoFitnessClassFoundException
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
@RequestMapping("/api/v1/fitness_class")
@Tag(name = "Fitness Class", description = "Endpoints for fitness classes")
class ClassesController(
    private val fitnessClassService: FitnessClassesService,
) {
    @Operation(
        summary = "Get fitness classes by day of week",
        description = "Retrieve all fitness classes by day of the week",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of fitness classes",
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Class not found",
                ),
            ],
    )
    @GetMapping
    suspend fun findAllFitnessClassesByDayOfWeek(
        @Parameter(
            description = "Day of the week",
            required = false,
            schema = Schema(type = "string"),
        )
        @RequestParam
        dayOfWeek: String? = null,
    ): Flow<FitnessClass> =
        try {
            fitnessClassService.fitnessClassesByDayOfWeek(dayOfWeek ?: "")
        } catch (exception: NoFitnessClassFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Class not found",
                exception,
            )
        }

    @Operation(summary = "Save a fitness class", description = "Save a new fitness class")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "Fitness class created successfully",
                ),
            ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFitnessClass(
        @Parameter(
            description = "Fitness class details to be saved",
            required = true,
            schema = Schema(implementation = FitnessClass::class),
        )
        @Valid
        @RequestBody
        fitnessClass: FitnessClass,
    ) = fitnessClassService.createFitnessClass(fitnessClass)

    @Operation(summary = "Update a fitness class", description = "Update an existing fitness class")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Fitness class updated successfully",
                ),
            ],
    )
    @PutMapping
    suspend fun updateFitnessClass(
        @Parameter(
            description = "Fitness class details to be updated",
            required = true,
            schema = Schema(implementation = FitnessClass::class),
        )
        @Valid
        @RequestBody
        fitnessClass: FitnessClass,
    ) = fitnessClassService.updateFitnessClass(fitnessClass)

    @Operation(
        summary = "Delete a fitness class by ID",
        description = "Delete a fitness class by its ID",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Fitness class deleted successfully",
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Class not found",
                ),
            ],
    )
    @DeleteMapping("/{id}")
    suspend fun deleteFitnessClassById(
        @Parameter(
            description = "ID of the fitness class to be deleted",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable
        id: String,
    ) = fitnessClassService.deleteFitnessClassById(id)
}
