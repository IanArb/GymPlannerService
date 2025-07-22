package com.ianarbuckle.gymplannerservice.faultReporting

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReport
import com.ianarbuckle.gymplannerservice.faultReporting.data.FaultReportService
import com.ianarbuckle.gymplannerservice.faultReporting.exception.FaultReportAlreadyExistsException

@RestController
@RequestMapping("/api/v1/fault")
@Tag(
    name = "Fault Reporting",
    description = "Endpoints for fault reporting",
)
class FaultReportController(
    private val faultReportService: FaultReportService,
) {
    @Operation(
        summary = "Get all fault reports",
        description = "Retrieve all fault reports",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of fault reports",
                ),
            ],
    )
    @GetMapping
    fun reports(): Flow<FaultReport> = faultReportService.reports()

    @Operation(
        summary = "Save a fault report",
        description = "Save a new fault report",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "201",
                    description = "Fault report created successfully",
                ),
                ApiResponse(
                    responseCode = "412",
                    description = "Precondition failed - Report already exists",
                ),
            ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveFaultReport(
        @Parameter(
            description = "Fault report details to be saved",
            required = true,
            schema = Schema(implementation = FaultReport::class),
        )
        @Valid
        @RequestBody
        faultReport: FaultReport,
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

    @Operation(summary = "Delete a fault report", description = "Delete a fault report by its ID")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "Fault report deleted successfully",
                ),
                ApiResponse(
                    responseCode = "404",
                    description = "Fault report not found",
                ),
            ],
    )
    @DeleteMapping("/{id}")
    suspend fun deleteReport(
        @Parameter(
            description = "ID of the fault report to be deleted",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable
        id: String,
    ) = faultReportService.deleteReportById(id)
}
