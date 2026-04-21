package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.checkin.data.CheckIn
import com.ianarbuckle.gymplannerservice.checkin.data.CheckInRequest
import com.ianarbuckle.gymplannerservice.checkin.data.CheckInStatus
import com.ianarbuckle.gymplannerservice.checkin.data.CheckOutRequest
import java.time.LocalDateTime

object CheckInDataProvider {
    fun createCheckIn(
        id: String? = "1",
        trainerId: String = "1",
        checkInTime: LocalDateTime = LocalDateTime.of(2026, 4, 21, 9, 0),
        checkOutTime: LocalDateTime? = null,
        status: CheckInStatus = CheckInStatus.ON_TIME,
    ): CheckIn =
        CheckIn(
            id = id,
            trainerId = trainerId,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
            status = status,
        )

    fun createCheckInRequest(
        checkInTime: LocalDateTime = LocalDateTime.of(2026, 4, 21, 9, 0),
    ): CheckInRequest = CheckInRequest(checkInTime = checkInTime)

    fun createCheckOutRequest(
        checkOutTime: LocalDateTime = LocalDateTime.of(2026, 4, 21, 17, 0),
    ): CheckOutRequest = CheckOutRequest(checkOutTime = checkOutTime)
}
