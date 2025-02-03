package com.ianarbuckle.gymplannerservice.mocks

import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

object FaultReportDataProvider {
    fun faultReports(): Flow<FaultReport> =
        flowOf(
            createFaultReport(),
            createFaultReport(
                id = "2",
                description = "Faulty machine 2",
                machineNumber = 456,
                photoUri = "https://www.gym-b.com",
                date = LocalDateTime.of(2021, 10, 10, 10, 10),
            ),
        )

    fun createFaultReport(
        id: String = "1",
        description: String = "Faulty machine",
        machineNumber: Int = 123,
        photoUri: String = "https://www.gym-a.com",
        date: LocalDateTime = LocalDateTime.of(2021, 10, 10, 10, 10),
    ): FaultReport =
        FaultReport(
            id = id,
            description = description,
            machineNumber = machineNumber,
            photoUri = photoUri,
            date = date,
        )
}
