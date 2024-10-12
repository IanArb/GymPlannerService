package com.ianarbuckle.gymplannerservice.faultReporting.data

import com.ianarbuckle.gymplannerservice.faultReporting.exception.ReportAlreadyExistsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

interface FaultReportService {
    fun reports(): Flow<Fault>
    suspend fun save(fault: Fault): Fault
    suspend fun deleteReportById(id: String)
}

@Service
class FaultReportServiceImpl(private val repository: FaultReportRepository) : FaultReportService {

    override fun reports(): Flow<Fault> {
        return repository.findAll()
    }

    override suspend fun save(fault: Fault): Fault {
        val reports = repository.findAll()

        reports.collect { report ->
            if (report.machineNumber == fault.machineNumber) {
                throw ReportAlreadyExistsException()
            }
        }

        return repository.save(fault)
    }

    override suspend fun deleteReportById(id: String) {
        repository.deleteById(id)
    }
}