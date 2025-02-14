package com.ianarbuckle.gymplannerservice.availability

import com.ianarbuckle.gymplannerservice.availability.data.Availability
import com.ianarbuckle.gymplannerservice.availability.data.AvailabilityService
import com.ianarbuckle.gymplannerservice.availability.data.CheckAvailability
import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@RequestMapping("/api/v1/availability")
@Tag(
    name = "Availability",
    description = "Endpoints for availability"
)
class AvailabilityController(
    private val availabilityService: AvailabilityService,
) {
    @Operation(
        summary = "Get availability",
        description = "Get availability for a personal trainer by month"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval of availability"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Availability or personal trainer not found"
            ),
        ],
    )
    @GetMapping("/{personalTrainerId}/{month}")
    suspend fun getAvailability(
        @Parameter(
            description = "Personal trainer id",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable personalTrainerId: String,
        @Parameter(
            description = "Month of the year",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable month: String,
    ): Availability =
        try {
            availabilityService.getAvailability(personalTrainerId, month)
        } catch (ex: AvailabilityNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found", ex)
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Personal trainer not found", ex)
        }

    @Operation(
        summary = "Save availability",
        description = "Save availability for a personal trainer"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Availability created successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Personal trainer not found"
            ),
        ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveAvailability(
        @Parameter(
            description = "Availability details to be saved",
            required = true,
            schema = Schema(implementation = Availability::class)
        )
        @RequestBody availability: Availability,
    ): Availability =
        try {
            availabilityService.saveAvailability(availability)
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Personal trainer not found", ex)
        }

    @Operation(
        summary = "Update availability",
        description = "Update availability for a personal trainer"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Availability updated successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Personal trainer not found"
            ),
        ],
    )
    @PutMapping
    suspend fun updateAvailability(
        @Parameter(
            description = "Availability details to be updated",
            required = true,
            schema = Schema(implementation = Availability::class),
        )
        @RequestBody availability: Availability,
    ) {
        availabilityService.updateAvailability(availability)
    }

    @Operation(
        summary = "Delete availability",
        description = "Delete availability by id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Availability deleted successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Availability not found"
            ),
        ],
    )
    @DeleteMapping("/{id}")
    suspend fun deleteAvailability(
        @Parameter(
            description = "Availability id",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable id: String,
    ) {
        availabilityService.deleteAvailability(id)
    }

    @Operation(
        summary = "Check availability",
        description = "Check availability for a personal trainer by month"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful check of availability"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Availability or personal trainer not found"
            ),
        ],
    )
    @GetMapping("/check_availability")
    suspend fun isAvailable(
        @Parameter(
            description = "Personal trainer id",
            required = true,
            schema = Schema(type = "string")
        )
        @RequestParam personalTrainerId: String,
        @Parameter(
            description = "Month of the year",
            required = true,
            schema = Schema(type = "string")
        )
        @RequestParam month: String,
    ): CheckAvailability =
        try {
            availabilityService.isAvailable(personalTrainerId, month)
        } catch (ex: AvailabilityNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Availability not found", ex)
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Personal trainer not found", ex)
        }
}
