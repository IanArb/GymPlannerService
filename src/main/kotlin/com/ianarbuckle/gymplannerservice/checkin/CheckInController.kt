package com.ianarbuckle.gymplannerservice.checkin

import com.ianarbuckle.gymplannerservice.checkin.data.CheckIn
import com.ianarbuckle.gymplannerservice.checkin.data.CheckInRequest
import com.ianarbuckle.gymplannerservice.checkin.data.CheckInService
import com.ianarbuckle.gymplannerservice.checkin.data.CheckOutRequest
import com.ianarbuckle.gymplannerservice.checkin.exception.InvalidCheckOutTimeException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerAlreadyCheckedOutException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotCheckedInException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotFoundException
import com.ianarbuckle.gymplannerservice.checkin.exception.TrainerNotScheduledException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/trainers")
@Tag(name = "Personal Trainer Check-In", description = "Endpoints for personal trainer check-in")
class CheckInController(
    private val service: CheckInService,
) {
    @Operation(
        summary = "Check in a personal trainer",
        description = "Records a check-in for a personal trainer at the start of their shift",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Check-in recorded successfully"),
                ApiResponse(
                    responseCode = "400",
                    description = "Trainer not scheduled or already checked in"
                ),
                ApiResponse(responseCode = "404", description = "Trainer not found"),
            ],
    )
    @PostMapping("/{id}/check-in")
    suspend fun checkIn(
        @Parameter(
            description = "Personal trainer ID",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable
        id: String,
        @RequestBody body: CheckInRequest,
    ): CheckIn =
        try {
            service.checkIn(id, body.checkInTime)
        } catch (ex: TrainerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found", ex)
        } catch (ex: TrainerNotScheduledException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Trainer is not scheduled to work at this time",
                ex
            )
        } catch (ex: TrainerAlreadyCheckedInException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Trainer has already checked in today",
                ex
            )
        }

    @Operation(
        summary = "Check out a personal trainer",
        description = "Records a check-out for a personal trainer at the end of their shift",
    )
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "Check-out recorded successfully"),
                ApiResponse(
                    responseCode = "400",
                    description = "Trainer has not checked in or already checked out",
                ),
                ApiResponse(responseCode = "404", description = "Trainer not found"),
            ],
    )
    @PostMapping("/{id}/check-out")
    suspend fun checkOut(
        @Parameter(
            description = "Personal trainer ID",
            required = true,
            schema = Schema(type = "string"),
        )
        @PathVariable
        id: String,
        @RequestBody body: CheckOutRequest,
    ): CheckIn =
        try {
            service.checkOut(id, body.checkOutTime)
        } catch (ex: TrainerNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Trainer not found", ex)
        } catch (ex: TrainerNotCheckedInException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Trainer has not checked in today",
                ex
            )
        } catch (ex: TrainerAlreadyCheckedOutException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Trainer has already checked out today",
                ex
            )
        } catch (ex: InvalidCheckOutTimeException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Check-out time must be after check-in time",
                ex
            )
        }
}
