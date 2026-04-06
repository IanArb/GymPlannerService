package com.ianarbuckle.gymplannerservice.facilityStatus

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@RequestMapping("/api/v1/facilities")
@Tag(
    name = "Facility Status",
    description = "Endpoints for managing gym facility and machine status"
)
class FacilityStatusController(
    private val service: FacilityStatusService,
) {
    @Operation(
        summary = "Get all machines",
        description = "Retrieve all gym machines across all locations"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of all machines"
                ),
            ],
    )
    @GetMapping
    suspend fun getAllMachines(): Flow<FacilityStatus> = service.findAllMachines()

    @Operation(
        summary = "Get facilities by gym location",
        description = "Retrieve all machines at a specific gym location",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of facilities"
                ),
            ],
    )
    @GetMapping(params = ["gymLocation"])
    suspend fun getFacilitiesByGymLocation(
        @Parameter(
            description = "Gym location",
            required = true,
            schema = Schema(implementation = GymLocation::class),
        )
        @RequestParam
        gymLocation: GymLocation,
    ): Flow<FacilityStatus> = service.findMachinesByGymLocation(gymLocation)

    @Operation(summary = "Get machine by ID", description = "Retrieve a single machine by its ID")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Machine found"),
                ApiResponse(responseCode = "404", description = "Machine not found"),
            ],
    )
    @GetMapping("/{id}")
    suspend fun getMachineById(
        @Parameter(
            description = "ID of the machine",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable
        id: String,
    ): FacilityStatus =
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")

    @Operation(
        summary = "Get facilities by status",
        description = "Retrieve all machines filtered by their operational status",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of facilities by status"
                ),
            ],
    )
    @GetMapping("/status/{status}")
    suspend fun getFacilityStatus(
        @Parameter(
            description = "Machine status",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable
        status: String,
    ): Flow<FacilityStatus> = service.findMachinesByStatus(status)

    @Operation(summary = "Create a facility", description = "Add a new machine to a gym location")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "201", description = "Facility created successfully"),
                ApiResponse(responseCode = "400", description = "Invalid request body"),
            ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createFacility(
        @Parameter(
            description = "Facility details to create",
            required = true,
            schema = Schema(implementation = FacilityStatus::class),
        )
        @RequestBody
        facilityStatus: FacilityStatus,
    ) {
        service.saveFacility(facilityStatus)
    }

    @Operation(
        summary = "Create facilities in batch",
        description = "Add multiple machines to a gym location in a single request",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "201", description = "Facilities created successfully"),
                ApiResponse(responseCode = "400", description = "Invalid request body"),
            ],
    )
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createFacilities(
        @Parameter(
            description = "List of facility details to create",
            required = true,
        )
        @RequestBody
        facilities: List<FacilityStatus>,
    ) {
        service.saveAllFacilities(facilities)
    }

    @Operation(
        summary = "Update a facility",
        description = "Update the details or status of an existing machine"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Facility updated successfully"),
                ApiResponse(responseCode = "404", description = "Machine not found"),
            ],
    )
    @PutMapping("/{id}")
    suspend fun updateFacility(
        @Parameter(
            description = "ID of the machine to update",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable
        id: String,
        @RequestBody facilityStatus: FacilityStatus,
    ): FacilityStatus {
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")
        return service.updateFacility(facilityStatus)
    }

    @Operation(
        summary = "Delete a facility by ID",
        description = "Remove a single machine by its ID"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "204", description = "Facility deleted successfully"),
                ApiResponse(responseCode = "404", description = "Machine not found"),
            ],
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteFacility(
        @Parameter(
            description = "ID of the machine to delete",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable
        id: String,
    ) {
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")
        service.deleteFacilityById(id)
    }

    @Operation(
        summary = "Delete all facilities by gym location",
        description = "Remove all machines at a specific gym location in one batch",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "204", description = "Facilities deleted successfully"),
            ],
    )
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteFacilitiesByGymLocation(
        @Parameter(
            description = "Gym location",
            required = true,
            schema = Schema(implementation = GymLocation::class),
        )
        @RequestParam
        gymLocation: GymLocation,
    ) {
        service.deleteAllFacilitiesByGymLocation(gymLocation)
    }
}
