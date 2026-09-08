package com.ianarbuckle.gymplannerservice.authentication.data.service

import com.ianarbuckle.gymplannerservice.authentication.data.domain.JwtResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.MessageResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.model.ERole
import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import com.ianarbuckle.gymplannerservice.authentication.data.model.UserProfile
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.common.UserNotFoundException
import com.ianarbuckle.gymplannerservice.security.JwtUtils
import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface AuthenticationService {
    suspend fun authenticationUser(loginRequest: LoginRequest): JwtResponse

    suspend fun createUser(signUpRequest: SignUpRequest): MessageResponse
}

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val userProfileRegistrar: UserProfileRegistrar,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
) : AuthenticationService {
    override suspend fun authenticationUser(loginRequest: LoginRequest): JwtResponse {
        val user =
            userRepository.findByUsername(loginRequest.username) ?: throw UserNotFoundException()

        if (encoder.matches(loginRequest.password, user.password)) {
            val jwt: String = jwtUtils.generateToken(user.username)

            val expiration = jwtUtils.extractExpiration(jwt).time

            return JwtResponse(
                userId = user.id,
                token = jwt,
                expiration = expiration,
            )
        } else {
            throw BadCredentialsException("Invalid username or password")
        }
    }

    override suspend fun createUser(signUpRequest: SignUpRequest): MessageResponse {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            throw UserAlreadyExistsException()
        }

        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw EmailAlreadyExistsException()
        }

        val strRoles = signUpRequest.roles
        val roles: MutableSet<ERole> =
            if (strRoles.isNullOrEmpty()) {
                mutableSetOf(ERole.ROLE_USER)
            } else {
                strRoles
                    .map { role ->
                        when (role) {
                            "admin" -> ERole.ROLE_ADMIN
                            "mod" -> ERole.ROLE_MODERATOR
                            else -> ERole.ROLE_USER
                        }
                    }.toMutableSet()
            }

        val userId = ObjectId().toHexString()

        val user =
            User(
                id = userId,
                username = signUpRequest.username,
                email = signUpRequest.email,
                password = encoder.encode(signUpRequest.password),
                roles = roles,
                pushNotificationToken = signUpRequest.pushNotificationToken,
            )

        userRepository.save(user)
        userProfileRegistrar.save(
            UserProfile(
                userId = userId,
                username = signUpRequest.username,
                firstName = signUpRequest.firstName,
                surname = signUpRequest.surname,
                email = signUpRequest.email,
                gymLocation = signUpRequest.gymLocation,
            ),
        )

        return MessageResponse("User registered successfully!")
    }
}
