package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.FacilityStatus
import com.ianarbuckle.gymplannerservice.facilityStatus.FaultType
import com.ianarbuckle.gymplannerservice.facilityStatus.Location
import com.ianarbuckle.gymplannerservice.facilityStatus.MachineStatus

object FacilityStatusDataProvider {
    fun createFacilityStatus(
        id: String? = "1",
        machineName: String = "Treadmill",
        machineNumber: Int = 1,
        gymLocation: GymLocation = GymLocation.CLONTARF,
        location: Location = Location.MAIN_GYM_FLOOR,
        faultType: FaultType = FaultType.MECHANICAL,
        status: MachineStatus = MachineStatus.OPERATIONAL,
    ): FacilityStatus =
        FacilityStatus(
            id = id,
            machineName = machineName,
            machineNumber = machineNumber,
            gymLocation = gymLocation,
            location = location,
            faultType = faultType,
            status = status,
        )
}
