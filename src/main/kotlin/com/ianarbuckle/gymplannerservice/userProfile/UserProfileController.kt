package com.ianarbuckle.gymplannerservice.userProfile

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/user_profile")
@Tag(
    name = "User Profile",
    description = "Endpoints for user profiles"
)
class UserProfileController(
    private val userProfileService: UserProfileService,
) {
    @Operation(
        summary = "Get user profile",
        description = "Retrieve a user profile by ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval of user profile"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            ),
        ],
    )
    @GetMapping("/{id}")
    suspend fun userProfile(
        @Parameter(
            description = "ID of the user",
            required = true,
            schema = Schema(type = "string")
        )
        @PathVariable id: String,
    ): UserProfile? {
        try {
            return userProfileService.userProfile(id)
        } catch (ex: UserNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found",
                ex,
            )
        }
    }

    @Operation(
        summary = "Update user profile",
        description = "Update an existing user profile"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User profile updated successfully"
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found"
            ),
        ],
    )
    @PutMapping
    suspend fun updateUserProfile(
        @Parameter(
            description = "User profile details to be updated",
            required = true,
            schema = Schema(implementation = UserProfile::class),
        )
        @RequestBody
        @Valid userProfile: UserProfile,
    ) {
        try {
            userProfileService.updateUserProfile(userProfile)
        } catch (ex: UserNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found",
                ex,
            )
        }
    }
}
