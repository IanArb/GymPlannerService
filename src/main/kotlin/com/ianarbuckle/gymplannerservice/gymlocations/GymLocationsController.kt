package com.ianarbuckle.gymplannerservice.gymlocations

import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocation
import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocationsService
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
class GymLocationsController(
    private val service: GymLocationsService,
) {
    @GetMapping
    suspend fun getAllGymLocations(): Flow<GymLocation> = service.findAllGymLocations()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createGymLocation(
        @RequestBody @Valid gymLocation: GymLocation,
    ): GymLocation = service.saveGymLocation(gymLocation)

    @PutMapping
    suspend fun updateGymLocation(
        @RequestBody @Valid gymLocation: GymLocation,
    ) {
        service.updateGymLocation(gymLocation)
    }

    @DeleteMapping("{id}")
    suspend fun deleteGymLocation(
        @PathVariable id: String,
    ) {
        service.deleteGymLocationById(id)
    }
}
