package com.ianarbuckle.gymplannerservice.facilityStatus

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FacilityStatusRepository : CoroutineCrudRepository<FacilityStatus, String> {

    suspend fun findMachinesByGymLocation(gymLocation: GymLocation): Flow<FacilityStatus>

    suspend fun findAllMachinesByStatus(machineStatus: String): Flow<FacilityStatus>

    suspend fun deleteAllByGymLocation(gymLocation: GymLocation)
}
