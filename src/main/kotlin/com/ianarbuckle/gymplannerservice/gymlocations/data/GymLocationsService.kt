package com.ianarbuckle.gymplannerservice.gymlocations.data

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

interface GymLocationsService {
    suspend fun findAllGymLocations(): Flow<GymLocation>

    suspend fun saveGymLocation(gymLocation: GymLocation): GymLocation

    suspend fun updateGymLocation(gymLocation: GymLocation)

    suspend fun deleteGymLocationById(id: String)
}

@Service
class GymLocationsServiceImpl(
    private val repository: GymLocationsRepository,
) : GymLocationsService {
    override suspend fun findAllGymLocations(): Flow<GymLocation> = repository.findAll()

    override suspend fun saveGymLocation(gymLocation: GymLocation): GymLocation =
        repository.save(gymLocation)

    override suspend fun updateGymLocation(gymLocation: GymLocation) {
        repository.existsById(gymLocation.id ?: "").also { repository.save(gymLocation) }
    }

    override suspend fun deleteGymLocationById(id: String) {
        repository.deleteById(id)
    }
}
