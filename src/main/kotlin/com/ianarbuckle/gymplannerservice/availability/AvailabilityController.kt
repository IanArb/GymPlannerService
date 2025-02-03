package com.ianarbuckle.gymplannerservice.availability

import com.ianarbuckle.gymplannerservice.availability.data.Availability
import com.ianarbuckle.gymplannerservice.availability.data.AvailabilityService
import com.ianarbuckle.gymplannerservice.availability.data.CheckAvailability
import com.ianarbuckle.gymplannerservice.availability.exception.AvailabilityNotFoundException
import com.ianarbuckle.gymplannerservice.booking.exception.PersonalTrainerNotFoundException
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
class AvailabilityController(
    private val availabilityService: AvailabilityService,
) {
    @GetMapping("/{personalTrainerId}/{month}")
    suspend fun getAvailability(
        @PathVariable personalTrainerId: String,
        @PathVariable month: String,
    ): Availability =
        try {
            availabilityService.getAvailability(personalTrainerId, month)
        } catch (ex: AvailabilityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Availability not found",
                ex,
            )
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Personal trainer not found",
                ex,
            )
        }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveAvailability(
        @RequestBody availability: Availability,
    ): Availability =
        try {
            availabilityService.saveAvailability(availability)
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Personal trainer not found",
                ex,
            )
        }

    @PutMapping
    suspend fun updateAvailability(
        @RequestBody availability: Availability,
    ) {
        availabilityService.updateAvailability(availability)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteAvailability(
        @PathVariable id: String,
    ) {
        availabilityService.deleteAvailability(id)
    }

    @GetMapping("/check_availability")
    suspend fun isAvailable(
        @RequestParam personalTrainerId: String,
        @RequestParam month: String,
    ): CheckAvailability =
        try {
            availabilityService.isAvailable(personalTrainerId, month)
        } catch (ex: AvailabilityNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Availability not found",
                ex,
            )
        } catch (ex: PersonalTrainerNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Personal trainer not found",
                ex,
            )
        }
}
