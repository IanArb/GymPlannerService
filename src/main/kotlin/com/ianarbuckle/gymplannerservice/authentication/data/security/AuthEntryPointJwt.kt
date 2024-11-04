package com.ianarbuckle.gymplannerservice.authentication.data.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {

    val logger: Logger = LoggerFactory.getLogger(AuthEntryPointJwt::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        logger.error("Unauthorized error: {}", authException?.message)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}