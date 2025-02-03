package com.ianarbuckle.gymplannerservice.userProfile

import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import com.ianarbuckle.gymplannerservice.userProfile.data.UserProfileService
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
@Tag(name = "User Profile", description = "Endpoints for user profiles")
class UserProfileController(
    private val userProfileService: UserProfileService,
) {
    @GetMapping("/{id}")
    suspend fun userProfile(
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

    @PutMapping()
    suspend fun updateUserProfile(
        @RequestBody @Valid userProfile: UserProfile,
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
