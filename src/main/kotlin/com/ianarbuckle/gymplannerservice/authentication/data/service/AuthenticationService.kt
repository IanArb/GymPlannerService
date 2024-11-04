package com.ianarbuckle.gymplannerservice.authentication.data.service

import com.ianarbuckle.gymplannerservice.authentication.data.domain.JwtResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.LoginRequest
import com.ianarbuckle.gymplannerservice.authentication.data.domain.MessageResponse
import com.ianarbuckle.gymplannerservice.authentication.data.domain.SignUpRequest
import com.ianarbuckle.gymplannerservice.authentication.data.exception.EmailAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.RoleNotFoundException
import com.ianarbuckle.gymplannerservice.authentication.data.exception.UserAlreadyExistsException
import com.ianarbuckle.gymplannerservice.authentication.data.model.ERole
import com.ianarbuckle.gymplannerservice.authentication.data.model.Role
import com.ianarbuckle.gymplannerservice.authentication.data.model.User
import com.ianarbuckle.gymplannerservice.authentication.data.model.UserAccount
import com.ianarbuckle.gymplannerservice.authentication.data.repository.RoleRepository
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserAccountRepository
import com.ianarbuckle.gymplannerservice.authentication.data.repository.UserRepository
import com.ianarbuckle.gymplannerservice.authentication.data.security.JwtUtils
import com.ianarbuckle.gymplannerservice.booking.exception.UserNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.function.Consumer


interface AuthenticationService {
    suspend fun authenticationUser(loginRequest: LoginRequest): JwtResponse
    suspend fun createUser(signUpRequest: SignUpRequest): MessageResponse
}

@Service
class AuthenticationServiceImpl(
    private val userRepository: UserRepository,
    private val userAccountRepository: UserAccountRepository,
    private val rolesRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
) : AuthenticationService {

    override suspend fun authenticationUser(loginRequest: LoginRequest): JwtResponse {
        val user = userRepository.findByUsername(loginRequest.username)
            ?: throw UserNotFoundException()

        if (encoder.matches(loginRequest.password, user.password)) {
            val jwt: String = jwtUtils.generateToken(user.username)

            return JwtResponse(
                token = jwt,
            )
        } else {
            throw BadCredentialsException("Invalid username or password");
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
        val roles: MutableSet<Role> = HashSet()

        if (strRoles?.isEmpty() == true) {
            val userRole: Role = rolesRepository.findByName(ERole.ROLE_USER)
                ?: throw RoleNotFoundException()
            roles.add(userRole)
        } else {
            strRoles?.map { role ->
                when (role) {
                    "admin" -> {
                        val adminRole: Role = rolesRepository.findByName(ERole.ROLE_ADMIN)
                            ?: throw RoleNotFoundException()
                        roles.add(adminRole)
                    }

                    "mod" -> {
                        val modRole: Role = rolesRepository.findByName(ERole.ROLE_MODERATOR)
                            ?: throw RoleNotFoundException()
                        roles.add(modRole)
                    }

                    else -> {
                        val userRole: Role = rolesRepository.findByName(ERole.ROLE_USER)
                            ?: throw RoleNotFoundException()
                        roles.add(userRole)
                    }
                }
            }
        }

        val user = User(
            username = signUpRequest.username,
            email = signUpRequest.email,
            password = encoder.encode(signUpRequest.password),
            roles = roles
        )

        userRepository.save(user)
        userAccountRepository.save(
            UserAccount(
                id = user.id ?: "",
                username = signUpRequest.username,
                firstName = signUpRequest.firstName,
                surname = signUpRequest.surname
            )
        )

        return MessageResponse("User registered successfully!")
    }
}