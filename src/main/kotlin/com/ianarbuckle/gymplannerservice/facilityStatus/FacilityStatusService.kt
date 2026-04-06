package com.ianarbuckle.gymplannerservice.facilityStatus

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.springframework.stereotype.Service

interface FacilityStatusService {
    suspend fun findMachinesByGymLocation(gymLocation: GymLocation): Flow<FacilityStatus>

    suspend fun findAllMachines(): Flow<FacilityStatus>

    suspend fun findMachinesByStatus(machineStatus: String): Flow<FacilityStatus>

    suspend fun findMachineById(id: String): FacilityStatus?

    suspend fun saveFacility(facilityStatus: FacilityStatus)

    suspend fun saveAllFacilities(facilities: List<FacilityStatus>)

    suspend fun updateFacility(facilityStatus: FacilityStatus): FacilityStatus

    suspend fun deleteFacilityById(id: String)
}

@Service
class FacilityStatusServiceImpl(private val repository: FacilityStatusRepository) :
    FacilityStatusService {

    override suspend fun findAllMachines(): Flow<FacilityStatus> = repository.findAll()

    override suspend fun findMachinesByGymLocation(gymLocation: GymLocation): Flow<FacilityStatus> =
        repository.findMachinesByGymLocation(gymLocation)

    override suspend fun findMachinesByStatus(machineStatus: String): Flow<FacilityStatus> =
        repository.findAllMachinesByStatus(machineStatus)

    override suspend fun findMachineById(id: String): FacilityStatus? = repository.findById(id)

    override suspend fun saveFacility(facilityStatus: FacilityStatus) {
        repository.save(facilityStatus)
    }

    override suspend fun saveAllFacilities(facilities: List<FacilityStatus>) {
        repository.saveAll(facilities).collect()
    }

    override suspend fun updateFacility(facilityStatus: FacilityStatus): FacilityStatus =
        repository.save(facilityStatus)

    override suspend fun deleteFacilityById(id: String) {
        repository.deleteById(id)
    }
}
