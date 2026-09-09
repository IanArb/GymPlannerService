package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.availability.data.AppointmentSlots
import com.ianarbuckle.gymplannerservice.availability.data.Availability
import com.ianarbuckle.gymplannerservice.availability.data.Status
import com.ianarbuckle.gymplannerservice.availability.data.Time
import java.time.LocalDate
import java.time.LocalTime

object AvailabilityDataProvider {
    fun createAvailability(
        personalTrainerId: String = "trainer1",
        month: String = "January",
        timeSlotId: String = "1",
        slots: List<AppointmentSlots> =
            createAppointmentSlots(
                timeSlotId = timeSlotId,
            ),
    ): Availability =
        Availability(
            personalTrainerId = personalTrainerId,
            month = month,
            slots = slots,
        )

    fun createAppointmentSlots(
        timeSlotId: String = "1",
        slotId: String = "1",
        date: LocalDate = LocalDate.of(2025, 12, 10),
        times: List<Time> =
            listOf<Time>(
                createTime(
                    id = timeSlotId,
                    status = Status.AVAILABLE,
                ),
            ),
    ): List<AppointmentSlots> =
        listOf(
            AppointmentSlots(
                id = slotId,
                date = date,
                times = times,
            ),
        )

    fun createTime(
        id: String = "1",
        startTime: LocalTime = LocalTime.of(8, 0),
        endTime: LocalTime = LocalTime.of(9, 0),
        status: Status = Status.AVAILABLE,
    ): Time =
        Time(
            id = id,
            startTime = startTime,
            endTime = endTime,
            status = status,
        )
}
