package com.ianarbuckle.gymplannerservice.facilityStatus

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
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
class FacilityStatusController(
    private val service: FacilityStatusService,
) {
    @GetMapping suspend fun getAllMachines(): Flow<FacilityStatus> = service.findAllMachines()

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

    @GetMapping("/{id}")
    suspend fun getMachineById(@PathVariable id: String): FacilityStatus =
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")

    @GetMapping("/status/{status}")
    suspend fun getFacilityStatus(@PathVariable status: String): Flow<FacilityStatus> =
        service.findMachinesByStatus(status)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createFacility(@RequestBody facilityStatus: FacilityStatus) {
        service.saveFacility(facilityStatus)
    }

    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createFacilities(@RequestBody facilities: List<FacilityStatus>) {
        service.saveAllFacilities(facilities)
    }

    @PutMapping("/{id}")
    suspend fun updateFacility(
        @PathVariable id: String,
        @RequestBody facilityStatus: FacilityStatus,
    ): FacilityStatus {
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")
        return service.updateFacility(facilityStatus)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteFacility(@PathVariable id: String) {
        service.findMachineById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Machine not found")
        service.deleteFacilityById(id)
    }
}
