package com.ianarbuckle.gymplannerservice.gymlocations

import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocation
import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocationsService
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/gym_locations")
@Tag(name = "Gym Locations", description = "Endpoints for gym locations")
class GymLocationsController(
    private val service: GymLocationsService,
) {
    @Operation(
        summary = "Get all gym locations",
        description = "Retrieve all gym locations",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval of gym locations",
            ),
        ],
    )
    @GetMapping
    suspend fun getAllGymLocations(): Flow<GymLocation> = service.findAllGymLocations()

    @Operation(
        summary = "Create a gym location",
        description = "Create a new gym location",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Gym location created successfully",
            ),
        ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createGymLocation(
        @Parameter(
            description = "Gym location details to be created",
            required = true,
            schema = Schema(implementation = GymLocation::class),
        )
        @RequestBody
        @Valid gymLocation: GymLocation,
    ): GymLocation = service.saveGymLocation(gymLocation)

    @Operation(
        summary = "Update a gym location",
        description = "Update an existing gym location",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Gym location updated successfully",
            ),
        ],
    )
    @PutMapping
    suspend fun updateGymLocation(
        @Parameter(
            description = "Gym location details to be updated",
            required = true,
            schema = Schema(implementation = GymLocation::class),
        )
        @RequestBody
        @Valid gymLocation: GymLocation,
    ) {
        service.updateGymLocation(gymLocation)
    }

    @Operation(summary = "Delete a gym location by ID", description = "Delete a gym location by its ID")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Gym location deleted successfully",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Gym location not found",
            ),
        ],
    )
    @DeleteMapping("{id}")
    suspend fun deleteGymLocation(
        @Parameter(
            description = "ID of the gym location to be deleted",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable id: String,
    ) {
        service.deleteGymLocationById(id)
    }
}
