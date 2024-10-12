package com.ianarbuckle.gymplannerservice.faultReporting

import com.ianarbuckle.gymplannerservice.faultReporting.data.Fault
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportService
import com.ianarbuckle.gymplannerservice.faultReporting.exception.ReportAlreadyExistsException
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/fault")
class FaultReportController(private val faultReportService: FaultReportService) {

    @GetMapping
    fun reports(): Flow<Fault> = faultReportService.reports()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFaultReport(@Valid @RequestBody fault: Fault): Fault {
        try {
            return faultReportService.save(fault)
        } catch (ex: ReportAlreadyExistsException) {
            throw ResponseStatusException(
                HttpStatus.PRECONDITION_FAILED, "Report already exists", ex
            )
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteReport(@PathVariable id: String) = faultReportService.deleteReportById(id)
}