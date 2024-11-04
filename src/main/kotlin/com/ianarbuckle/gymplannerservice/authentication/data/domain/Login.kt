package com.ianarbuckle.gymplannerservice.authentication.data.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
)

data class SignUpRequest(
    @NotBlank
    @Size(min = 3, max = 20)
    val username: String,
    @NotBlank
    val firstName: String,
    @NotBlank
    val surname: String,
    @NotBlank
    @Size(max = 50)
    @Email
    val email: String,
    @NotBlank
    @Size(min = 6, max = 40)
    val password: String,
    val roles: Set<String>? = emptySet(),
)
