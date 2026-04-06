package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.common.GymLocation
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FacilityStatus
import com.ianarbuckle.gymplannerservice.facilityStatus.data.FaultType
import com.ianarbuckle.gymplannerservice.facilityStatus.data.Location
import com.ianarbuckle.gymplannerservice.facilityStatus.data.MachineStatus

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
            _id = id,
            machineName = machineName,
            machineNumber = machineNumber,
            gymLocation = gymLocation,
            location = location,
            faultType = faultType,
            status = status,
        )
}
