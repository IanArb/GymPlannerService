package com.ianarbuckle.gymplannerservice.faultReporting

import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReport
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportService
import com.ianarbuckle.gymplannerservice.faultReporting.exception.FaultReportAlreadyExistsException
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/fault")
@Tag(name = "Fault Reporting", description = "Endpoints for fault reporting")
class FaultReportController(
    private val faultReportService: FaultReportService,
) {
    @GetMapping
    fun reports(): Flow<FaultReport> = faultReportService.reports()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFaultReport(
        @Valid @RequestBody faultReport: FaultReport,
    ): FaultReport {
        try {
            return faultReportService.save(faultReport)
        } catch (ex: FaultReportAlreadyExistsException) {
            throw ResponseStatusException(
                HttpStatus.PRECONDITION_FAILED,
                "Report already exists",
                ex,
            )
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteReport(
        @PathVariable id: String,
    ) = faultReportService.deleteReportById(id)
}
