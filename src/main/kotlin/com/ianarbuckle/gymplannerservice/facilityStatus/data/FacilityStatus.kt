package com.ianarbuckle.gymplannerservice.facilityStatus.data

import com.ianarbuckle.gymplannerservice.common.GymLocation
import jakarta.validation.Valid
import org.bson.codecs.pojo.annotations.BsonId

@Suppress("ConstructorParameterNaming")
data class FacilityStatus(
    @BsonId val _id: String? = null,
    val machineName: String,
    val machineNumber: Int,
    @Valid val gymLocation: GymLocation,
    val location: Location,
    val faultType: FaultType,
    val status: MachineStatus,
)

enum class MachineStatus {
    OPERATIONAL,
    OUT_OF_ORDER,
    UNDER_MAINTENANCE
}

enum class Location {
    MAIN_GYM_FLOOR,
    BLUE_GYM_FLOOR,
    FREE_WEIGHTS_AREA,
    BOX_GYM_FLOOR,
}

enum class FaultType {
    MECHANICAL,
    ELECTRICAL,
    SOFTWARE,
    OTHER
}
