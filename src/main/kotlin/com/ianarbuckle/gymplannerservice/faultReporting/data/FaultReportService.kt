package com.ianarbuckle.gymplannerservice.faultReporting.data

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import com.ianarbuckle.gymplannerservice.faultReporting.exception.FaultReportAlreadyExistsException

interface FaultReportService {
    fun reports(): Flow<FaultReport>

    suspend fun save(faultReport: FaultReport): FaultReport

    suspend fun deleteReportById(id: String)
}

@Service
class FaultReportServiceImpl(
    private val repository: FaultReportRepository,
) : FaultReportService {
    override fun reports(): Flow<FaultReport> = repository.findAll()

    override suspend fun save(faultReport: FaultReport): FaultReport {
        val reports = repository.findAll()

        reports.collect { report ->
            if (report.machineNumber == faultReport.machineNumber) {
                throw FaultReportAlreadyExistsException()
            }
        }

        return repository.save(faultReport)
    }

    override suspend fun deleteReportById(id: String) {
        repository.deleteById(id)
    }
}
