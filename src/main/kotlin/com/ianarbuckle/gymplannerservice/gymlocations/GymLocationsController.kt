package com.ianarbuckle.gymplannerservice.gymlocations

import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocation
import com.ianarbuckle.gymplannerservice.gymlocations.data.GymLocationsService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/gym_locations")
class GymLocationsController(private val service: GymLocationsService) {

    @GetMapping
    suspend fun getAllGymLocations(): Flow<GymLocation> = service.findAllGymLocations()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createGymLocation(@RequestBody @Valid gymLocation: GymLocation): GymLocation {
        return service.saveGymLocation(gymLocation)
    }

    @PutMapping
    suspend fun updateGymLocation(@RequestBody @Valid gymLocation: GymLocation) {
        service.updateGymLocation(gymLocation)
    }

    @DeleteMapping("{id}")
    suspend fun deleteGymLocation(@PathVariable id: String) {
        service.deleteGymLocationById(id)
    }

}