package com.ianarbuckle.gymplannerservice.authentication

import com.ianarbuckle.gymplannerservice.authentication.data.domain.JwtResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.MessageResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.RoleNotFoundException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.service.AuthenticationService
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication",
    description = "Endpoints for authentication",
)
class AuthenticationController(
    private val authenticationService: AuthenticationService,
) {
    @Operation(
        summary = "Authenticate user",
        description = "Authenticate user with username and password",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful authentication",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid username or password",
            ),
            ApiResponse(
                responseCode = "404",
                description = "User not found",
            ),
        ],
    )
    @PostMapping("/login")
    suspend fun authenticateUser(
        @RequestBody @Valid loginRequest: LoginRequest,
    ): JwtResponse =
        try {
            authenticationService.authenticationUser(loginRequest)
        } catch (ex: UserNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "User not found",
                ex,
            )
        } catch (ex: BadCredentialsException) {
            throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                ex,
            )
        }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "User registered successfully",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request - User already exists or invalid role",
            ),
        ],
    )
    @PostMapping("/register")
    suspend fun registerUser(
        @RequestBody @Valid signUpRequest: SignUpRequest,
    ): MessageResponse =
        try {
            authenticationService.createUser(signUpRequest)
        } catch (ex: UserAlreadyExistsException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User already exists",
                ex,
            )
        } catch (ex: EmailAlreadyExistsException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Email already exists",
                ex,
            )
        } catch (ex: RoleNotFoundException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Role already exists",
                ex,
            )
        }
}
